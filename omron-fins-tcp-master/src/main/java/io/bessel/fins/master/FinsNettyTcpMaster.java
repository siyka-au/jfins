package io.bessel.fins.master;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bessel.fins.Bit;
import io.bessel.fins.FinsEndCode;
import io.bessel.fins.FinsFrame;
import io.bessel.fins.FinsFrameBuilder;
import io.bessel.fins.FinsIoAddress;
import io.bessel.fins.FinsMaster;
import io.bessel.fins.FinsMasterException;
import io.bessel.fins.FinsNodeAddress;
import io.bessel.fins.codec.FinsFrameCodec;
import io.bessel.fins.codec.FinsTcpFrameCodec;
import io.bessel.fins.commands.FinsMemoryAreaReadCommand;
import io.bessel.fins.commands.FinsMemoryAreaReadWordResponse;
import io.bessel.fins.commands.FinsMemoryAreaWriteResponse;
import io.bessel.fins.commands.FinsMemoryAreaWriteWordCommand;
import io.bessel.fins.master.handlers.FinsMasterHandler;
import io.bessel.fins.master.handlers.FinsTcpMasterCommandHandler;
import io.bessel.fins.master.handlers.FinsTcpMasterHandshakeHandler;
import io.bessel.fins.master.handlers.FinsTcpPayloadHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FinsNettyTcpMaster implements FinsMaster {

	final static Logger logger = LoggerFactory.getLogger(FinsNettyTcpMaster.class);

	private final String host;
	private final Integer port;
	private final FinsNodeAddress nodeAddress;
	private final byte serviceAddress = 0x12;

	private Bootstrap bootstrap;
	private EventLoopGroup workerGroup;
	private Channel channel;

	private BlockingQueue<CompletableFuture<FinsFrame>> queue = new ArrayBlockingQueue<>(1);

	public FinsNettyTcpMaster(String host, Integer port, FinsNodeAddress nodeAddress) {
		this.host = host;
		this.port = port;
		this.nodeAddress = nodeAddress;
	}

	// API implementation
	@Override
	public void connect() {
		try {
			this.workerGroup = new NioEventLoopGroup();

			this.bootstrap = new Bootstrap();
			this.bootstrap.group(workerGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel channel) throws Exception {
							channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG))
									// ByteBuf
									.addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4))
									// chunked ByteBuf
									.addLast(new FinsTcpFrameCodec())
									// FINS/TCP
									.addLast(new FinsTcpMasterHandshakeHandler(FinsNettyTcpMaster.this))
									.addLast(new FinsTcpMasterCommandHandler()).addLast(new FinsTcpPayloadHandler())
									// ByteBuf
									.addLast(new FinsFrameCodec())
									// FINS
									.addLast(new FinsMasterHandler(FinsNettyTcpMaster.this));
						}
					}).option(ChannelOption.SO_KEEPALIVE, true);
			CompletableFuture<FinsFrame> future = new CompletableFuture<>();
			this.queue.put(future);

			ChannelFuture channelFuture = bootstrap.connect(this.host, this.port).sync();
			this.channel = channelFuture.channel();
			future.get();
		} catch (InterruptedException | ExecutionException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		}
	}

	@Override
	public void disconnect() {
		try {
			this.queue.put(new CompletableFuture<FinsFrame>());
			this.workerGroup.shutdownGracefully().sync();
			this.queue.remove();
		} catch (InterruptedException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		}
	}

	@Override
	public short readWord(FinsNodeAddress destination, FinsIoAddress address) {
		return readWords(destination, address, 1).get(0);
	}

	@Override
	public List<Short> readWords(FinsNodeAddress destination, FinsIoAddress address, short itemCount) {
		FinsMemoryAreaReadCommand command = new FinsMemoryAreaReadCommand(address, itemCount);

		FinsFrame frame = new FinsFrameBuilder().setDestinationAddress(destination).setSourceAddress(this.nodeAddress)
				.setServiceAddress(this.serviceAddress).setData(command.getBytes()).build();

		FinsFrame replyFrame = this.send(frame);
		FinsMemoryAreaReadWordResponse response = FinsMemoryAreaReadWordResponse.parseFrom(replyFrame.getData(),
				itemCount);
		List<Short> items = response.getItems();

		return items;
	}

	@Override
	public List<Short> readWords(FinsNodeAddress destination, FinsIoAddress address, int itemCount) {
		return readWords(destination, address, (short) itemCount);
	}

	@Override
	public Bit readBit(FinsNodeAddress destination, FinsIoAddress address) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public List<Bit> readBits(FinsNodeAddress destination, FinsIoAddress address, short itemCount) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public List<Bit> readBits(FinsNodeAddress destination, FinsIoAddress address, int itemCount) {
		return readBits(destination, address, (short) itemCount);
	}

	@Override
	public List<Short> readMultipleWords(FinsNodeAddress destination, List<FinsIoAddress> addresses) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void writeWord(FinsNodeAddress destination, FinsIoAddress address, short value) {
		List<Short> items = new ArrayList<Short>();
		items.add(value);
		writeWords(destination, address, items);
	}

	@Override
	public void writeWords(FinsNodeAddress destination, FinsIoAddress address, List<Short> items) {
		FinsMemoryAreaWriteWordCommand command = new FinsMemoryAreaWriteWordCommand(address, items);

		FinsFrame frame = new FinsFrameBuilder().setDestinationAddress(destination).setSourceAddress(this.nodeAddress)
				.setServiceAddress(this.serviceAddress).setData(command.getBytes()).build();

		FinsFrame replyFrame = this.send(frame);
		FinsMemoryAreaWriteResponse response = FinsMemoryAreaWriteResponse.parseFrom(replyFrame.getData());

		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
			// TODO We have not had normal completion, maybe get upset and throw
			// an exception
		}
	}

	@Override
	public void writeMultipleWords(FinsNodeAddress destination, List<FinsIoAddress> addresses, List<Short> values) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	// Internal methods
	private FinsFrame send(FinsFrame frame) {
		CompletableFuture<FinsFrame> future = new CompletableFuture<>();

		try {
			this.queue.put(future);
			this.channel.writeAndFlush(frame);
			FinsFrame replyFrame = future.get();
			return replyFrame;
		} catch (InterruptedException | ExecutionException e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	// Getters and setters
	public BlockingQueue<CompletableFuture<FinsFrame>> getQueue() {
		return this.queue;
	}

	@Override
	public String readString(FinsNodeAddress destination, FinsIoAddress address, int wordLength)
			throws FinsMasterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readString(FinsNodeAddress destination, FinsIoAddress address, short wordLength)
			throws FinsMasterException {
		// TODO Auto-generated method stub
		return null;
	}

}
