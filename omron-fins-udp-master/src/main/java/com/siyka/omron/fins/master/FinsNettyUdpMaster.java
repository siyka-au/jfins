package com.siyka.omron.fins.master;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FinsNettyUdpMaster implements FinsMaster {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final InetSocketAddress destinationAddress;
	private final InetSocketAddress sourceAddress;
	private final FinsNodeAddress nodeAddress;
	
	private NioEventLoopGroup workerGroup;
	private Bootstrap bootstrap;
	private Channel channel;
	
	private final AtomicInteger serviceAddress = new AtomicInteger(0);
	
	private CompletableFuture<FinsFrame> sendFuture;

	// TODO make configurable
	private int retries = 3;
	
	public FinsNettyUdpMaster(InetSocketAddress destinationAddress, InetSocketAddress sourceAddress, FinsNodeAddress nodeAddress) {
		this.nodeAddress = nodeAddress;
		
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
						.addLast(new FinsMasterHandler(FinsNettyUdpMaster.this));
				}
			});
		
		this.destinationAddress = destinationAddress;
		this.sourceAddress = sourceAddress;
	}
	
	// FINS Master API
	@Override
	public void connect() throws FinsMasterException {
		if (bootstrap == null) {
			throw new FinsMasterException("FINS master bootstrap not correctly set");
		}
		
		try {
			this.channel = bootstrap.connect(this.destinationAddress, this.sourceAddress).sync().channel();
		} catch (InterruptedException ex) {
			throw new FinsMasterException("FINS master connection operation interrupted", ex);
		}
	}

	@Override
	public void disconnect() {
		Optional.of(this.bootstrap.group()).ifPresent(g -> {
			try {
				g.shutdownGracefully().sync();
			} catch (InterruptedException ex) {
				logger.error("FINS master channel close operation interrupted", ex);
			}
		});
	}

	@Override
	public String readString(FinsNodeAddress destination, FinsIoAddress address, int wordLength) throws FinsMasterException {
		return readString(destination, address, (short) wordLength);
	}
	
	@Override
	public String readString(FinsNodeAddress destination, FinsIoAddress address, short wordLength) throws FinsMasterException {
		List<Short> words = this.readWords(destination, address, wordLength);
		StringBuffer stringBuffer = new StringBuffer(wordLength * 2);
		byte[] bytes = new byte[2];
		for (Short s : words) {
			bytes[1] = (byte)(s & 0xff);
			bytes[0] = (byte)((s >> 8) & 0xff);
			try {
				stringBuffer.append(new String(bytes, "US-ASCII"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return stringBuffer.toString();
	}
	
	@Override
	public short readWord(FinsNodeAddress destination, FinsIoAddress address) throws FinsMasterException {	
		return readWords(destination, address, 1).get(0);
	}

	@Override
	public List<Short> readWords(FinsNodeAddress destination, FinsIoAddress address, short itemCount) throws FinsMasterException {		
		MemoryAreaReadCommand command = new MemoryAreaReadCommand(address, itemCount);
		
		FinsFrame frame = new FinsFrameBuilder()
			.setDestinationAddress(destination)
			.setSourceAddress(this.nodeAddress)
			.setServiceAddress(this.getNextServiceAddress())
			.setData(command.getBytes())
			.build();
		
		FinsFrame replyFrame = this.send(frame);
		byte[] data = replyFrame.getData();
		MemoryAreaReadWordResponse response = MemoryAreaReadWordResponse.parseFrom(data, itemCount);
		List<Short> items = response.getItems();
		
		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
			throw new FinsMasterException(String.format("%s", response.getEndCode()));
		}
		
		return items;
	}

	@Override
	public List<Short> readWords(FinsNodeAddress destination, FinsIoAddress address, int itemCount) throws FinsMasterException {
		return readWords(destination, address, (short) itemCount);
	}
	
	@Override
	public Bit readBit(FinsNodeAddress destination, FinsIoAddress address) throws FinsMasterException {
		return readBits(destination, address, 1).get(0);
	}
	
	@Override
	public List<Bit> readBits(FinsNodeAddress destination, FinsIoAddress address, short itemCount) throws FinsMasterException {
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
	public List<Bit> readBits(FinsNodeAddress destination, FinsIoAddress address, int itemCount) throws FinsMasterException {
		return readBits(destination, address, (short) itemCount);
	}

	@Override
	public List<Short> readMultipleWords(FinsNodeAddress destination, List<FinsIoAddress> addresses) throws FinsMasterException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void writeWord(FinsNodeAddress destination, FinsIoAddress address, short value) throws FinsMasterException {
		List<Short> items = new ArrayList<Short>();
		items.add(value);
		writeWords(destination, address, items);
	}

	@Override
	public void writeWords(FinsNodeAddress destination, FinsIoAddress address, List<Short> items) throws FinsMasterException {
		MemoryAreaWriteWordCommand command = new MemoryAreaWriteWordCommand(address, items);
		
		FinsFrame frame = new FinsFrameBuilder()
			.setDestinationAddress(destination)
			.setSourceAddress(this.nodeAddress)
			.setServiceAddress(this.getNextServiceAddress())
			.setData(command.getBytes())
			.build();
		
		FinsFrame replyFrame = this.send(frame);
		MemoryAreaWriteResponse response = MemoryAreaWriteResponse.parseFrom(replyFrame.getData());
		
		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
			throw new FinsMasterException(String.format("%s", response.getEndCode()));
		}
	}

	@Override
	public void writeMultipleWords(FinsNodeAddress destination, List<FinsIoAddress> addresses, List<Short> values) throws FinsMasterException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	private synchronized FinsFrame send(FinsFrame frame) {
		return this.send(frame, 0);
	}
	
	// Internal methods
	private synchronized FinsFrame send(FinsFrame frame, int attempt) {
		logger.debug("Sending FinsFrame");

		try {
			this.sendFuture = new CompletableFuture<>();
			logger.debug("Write and flush FinsFrame");
			this.channel.writeAndFlush(frame);
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
	private byte getNextServiceAddress() {
		return (byte) this.serviceAddress.incrementAndGet();
	}
	
	protected CompletableFuture<FinsSimpleFrame> getSendFuture() {
		return sendFuture;
	}

	public void writeBit(FinsNodeAddress destination, FinsIoAddress address, Boolean value) throws FinsMasterException {
		MemoryAreaWriteBitCommand command = new MemoryAreaWriteBitCommand(address, value);
		
		FinsFrame frame = new FinsFrameBuilder()
			.setDestinationAddress(destination)
			.setSourceAddress(this.nodeAddress)
			.setServiceAddress(this.getNextServiceAddress())
			.setData(command.getBytes())
			.build();
		
		FinsFrame replyFrame = this.send(frame);
		MemoryAreaWriteResponse response = MemoryAreaWriteResponse.parseFrom(replyFrame.getData());
		
		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
			throw new FinsMasterException(String.format("%s", response.getEndCode()));
		}
		
	}

	@Override
	public void close() throws Exception {
		this.disconnect();
	}
	
}
