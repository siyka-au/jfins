package com.siyka.omron.fins.master;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.FinsSimpleFrame;
import com.siyka.omron.fins.codec.FinsFrameBuilder;
import com.siyka.omron.fins.codec.FinsFrameUdpCodec;
import com.siyka.omron.fins.commands.MemoryAreaWriteBitCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteWordCommand;
import com.siyka.omron.fins.responses.MemoryAreaWriteResponse;
import com.siyka.omron.fins.wip.MemoryAreaReadBitResponse;
import com.siyka.omron.fins.wip.MemoryAreaReadCommand;
import com.siyka.omron.fins.wip.MemoryAreaReadWordResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.CompleteFuture;

public class FinsNettyUdpMaster implements FinsMaster {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final FinsNodeAddress nodeAddress;
	private final InetSocketAddress destinationAddress;
	private final InetSocketAddress sourceAddress;
	

	private final NioEventLoopGroup workerGroup;
	private final Bootstrap bootstrap;
	private Channel channel;

	private final AtomicInteger serviceAddress = new AtomicInteger(0);

	private final Map<Byte, CompletableFuture<FinsFrame>> futures;

	// TODO make configurable
	private int retries = 3;

	public FinsNettyUdpMaster(final InetSocketAddress destinationAddress, final InetSocketAddress sourceAddress, final FinsNodeAddress nodeAddress) {
		this.nodeAddress = nodeAddress;
		this.sourceAddress = sourceAddress;
		this.destinationAddress = destinationAddress;

		this.futures = new HashMap<>();

		this.workerGroup = new NioEventLoopGroup();
		this.bootstrap = new Bootstrap();
		this.bootstrap.group(this.workerGroup)
				.channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(new ChannelInitializer<DatagramChannel>() {
					@Override
					public void initChannel(DatagramChannel channel) throws Exception {
						channel.pipeline()
							.addLast(new LoggingHandler(LogLevel.DEBUG))
							.addLast(new FinsFrameUdpCodec())
							.addLast(new FinsMasterHandler(FinsNettyUdpMaster.this.futures));
					}
				});
	}

	// FINS Master API
	@Override
	public CompletableFuture<Void> connect() {
		final CompletableFuture<Void> connectFuture = new CompletableFuture<>();
		this.bootstrap.connect(this.destinationAddress, this.sourceAddress).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				FinsNettyUdpMaster.this.channel = future.channel();
				connectFuture.complete(null);
			}
		});
		return connectFuture;
	}

	@Override
	public CompletableFuture<Void> disconnect() {
		final CompletableFuture<Void> disconnectFuture = new CompletableFuture<>();
		this.workerGroup.shutdownGracefully().addListener(f -> disconnectFuture.complete(null));
		return disconnectFuture;
	}

	@Override
	public CompletableFuture<String> readString(final FinsNodeAddress destination, final FinsIoAddress address, final int wordLength) {
		return readString(destination, address, (short) wordLength);
	}

	@Override
	public CompletableFuture<String> readString(final FinsNodeAddress destination, final FinsIoAddress address, final short wordLength) {
		return this.readWords(destination, address, wordLength)
				.handleAsync((words, throwable) -> {
					StringBuffer stringBuffer = new StringBuffer(wordLength * 2);
					byte[] bytes = new byte[2];
					for (Short s : words) {
						bytes[1] = (byte) (s & 0xff);
						bytes[0] = (byte) ((s >> 8) & 0xff);
						try {
							stringBuffer.append(new String(bytes, "US-ASCII"));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					return stringBuffer.toString();
				});
	}

	@Override
	public CompletableFuture<Short> readWord(final FinsNodeAddress destination, final FinsIoAddress address) {
		return readWords(destination, address, 1).handleAsync((words, throwable) -> words.get(0));
	}

	@Override
	public CompletableFuture<List<Short>> readWords(final FinsNodeAddress destination, final FinsIoAddress address, final short itemCount) {
		MemoryAreaReadCommand command = new MemoryAreaReadCommand(address, itemCount);

		final FinsFrame frame = FinsFrame.Builder().setDestinationAddress(destination)
				.setSourceAddress(this.nodeAddress).setServiceAddress(this.getNextServiceAddress())
				.setData(command.getBytes()).build();

		FinsFrame replyFrame = this.send(frame);
		byte[] data = replyFrame.getPdu();
		MemoryAreaReadWordResponse response = MemoryAreaReadWordResponse.parseFrom(data, itemCount);
		List<Short> items = response.getItems();

		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
			throw new FinsMasterException(String.format("%s", response.getEndCode()));
		}

		return items;
	}

	@Override
	public CompletableFuture<List<Short>> readWords(final FinsNodeAddress destination, final FinsIoAddress address, final int itemCount) {
		return readWords(destination, address, (short) itemCount);
	}

	@Override
	public CompletableFuture<Bit> readBit(final FinsNodeAddress destination, final FinsIoAddress address) {
		return readBits(destination, address, 1).handleAsync((bits, throwable) -> bits.get(0));
	}

	@Override
	public CompletableFuture<List<Bit>> readBits(final FinsNodeAddress destination, final FinsIoAddress address, final short itemCount) {
		MemoryAreaReadCommand command = new MemoryAreaReadCommand(address, itemCount);
		
		FinsFrame frame = new FinsFrameBuilder()
			.setDestinationAddress(destination)
			.setSourceAddress(this.nodeAddress)
			.setServiceAddress(this.getNextServiceAddress())
			.setData(command.getBytes())
			.build();
		
		FinsFrame replyFrame = this.send(frame);
		byte[] data = replyFrame.getData();
		MemoryAreaReadBitResponse response = MemoryAreaReadBitResponse.parseFrom(data, itemCount);
		List<Bit> items = response.getItems();
		
		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
			throw new FinsMasterException(String.format("%s", response.getEndCode()));
		}
		
		return items;
	}

	@Override
	public CompletableFuture<List<Bit>> readBits(final FinsNodeAddress destination, final FinsIoAddress address, final int itemCount) {
		return readBits(destination, address, (short) itemCount);
	}

	@Override
	public CompletableFuture<List<Short>> readMultipleWords(final FinsNodeAddress destination, final List<FinsIoAddress> addresses) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public CompletableFuture<Void> writeWord(final FinsNodeAddress destination, final FinsIoAddress address, final short value) {
		List<Short> items = new ArrayList<Short>();
		items.add(value);
		writeWords(destination, address, items);
	}

	@Override
	public CompletableFuture<Void> writeWords(final FinsNodeAddress destination, final FinsIoAddress address, final List<Short> items) {
		MemoryAreaWriteWordCommand command = new MemoryAreaWriteWordCommand(address, items);

		FinsFrame frame = new FinsFrameBuilder().setDestinationAddress(destination).setSourceAddress(this.nodeAddress)
				.setServiceAddress(this.getNextServiceAddress()).setData(command.getBytes()).build();

		FinsFrame replyFrame = this.send(frame);
		MemoryAreaWriteResponse response = MemoryAreaWriteResponse.parseFrom(replyFrame.getData());

		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
			throw new FinsMasterException(String.format("%s", response.getEndCode()));
		}
	}

	@Override
	public CompletableFuture<Void> writeMultipleWords(final FinsNodeAddress destination, final List<FinsIoAddress> addresses, final List<Short> values) throws FinsMasterException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	private CompletableFuture<FinsFrame> send(final FinsFrame frame) {
		return this.send(frame, 0);
	}

	// Internal methods
	private CompletableFuture<FinsFrame> send(final FinsFrame frame, final int attempt) {
		logger.debug("Sending FinsFrame");
		final CompletableFuture<FinsFrame> future = new CompletableFuture<>();
		this.futures.put(frame.getHeader().getServiceAddress(), future);
		this.channel.writeAndFlush(frame);
		
		
		
		return future;
		try {
			
			logger.debug("Write and flush FinsFrame");
			
			logger.debug("Awaiting future to be completed");
			FinsFrame replyFrame = this.sendFuture.get(1000, TimeUnit.MILLISECONDS);
			logger.debug("Future compeleted");
			return replyFrame;
		} catch (TimeoutException e) {
			if (attempt < this.retries) {
				return send(frame, attempt++);
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	// Getters and setters
	private synchronized byte getNextServiceAddress() {
		return (byte) this.serviceAddress.incrementAndGet();
	}

	public CompletableFuture<Void> writeBit(FinsNodeAddress destination, FinsIoAddress address, Boolean value)
			throws FinsMasterException {
		MemoryAreaWriteBitCommand command = new MemoryAreaWriteBitCommand(address, value);

		FinsFrame frame = new FinsFrameBuilder().setDestinationAddress(destination).setSourceAddress(this.nodeAddress)
				.setServiceAddress(this.getNextServiceAddress()).setData(command.getBytes()).build();

		FinsFrame replyFrame = this.send(frame);
		MemoryAreaWriteResponse response = MemoryAreaWriteResponse.parseFrom(replyFrame.getData());

		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
			throw new FinsMasterException(String.format("%s", response.getEndCode()));
		}

	}

	@Override
	public void close() throws Exception {
		this.disconnect().get();
	}

}
