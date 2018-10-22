package com.siyka.omron.fins.master;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsCommand;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.FinsResponse;
import com.siyka.omron.fins.MemoryAreaReadCommand;
import com.siyka.omron.fins.MemoryAreaReadWordsResponse;
import com.siyka.omron.fins.MemoryAreaWriteWordsCommand;
import com.siyka.omron.fins.SimpleResponse;
import com.siyka.omron.fins.Word;

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

public class FinsNettyUdpMaster implements FinsMaster {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final FinsNodeAddress nodeAddress;
	private final InetSocketAddress destinationAddress;
	private final InetSocketAddress sourceAddress;

	private final NioEventLoopGroup workerGroup;
	private final Bootstrap bootstrap;
	private Channel channel;

	private final AtomicInteger serviceAddress = new AtomicInteger(0);

	private final Map<Byte, CompletableFuture<FinsFrame<FinsResponse>>> futures;

	// TODO make configurable
//	private int retries = 3;

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
								.addLast(new FinsFrameUdpCodec(destinationAddress, sourceAddress))
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
				FinsNettyUdpMaster.this.channel = future.sync().channel();
				connectFuture.complete(null);
			}
		});
		return connectFuture;
	}

	@Override
	public CompletableFuture<Void> disconnect() {
		final CompletableFuture<Void> disconnectFuture = new CompletableFuture<>();
		this.workerGroup.shutdownGracefully().addListener(f -> {
			f.sync();
			disconnectFuture.complete(null);
		});
		return disconnectFuture;
	}
	
	@Override
	public CompletableFuture<List<Word>> readWords(final FinsNodeAddress destination, final FinsIoAddress address, final short itemCount) {
		final FinsHeader header = FinsHeader.Builder.defaultCommandBuilder()
				.setDestinationAddress(destination)
				.setSourceAddress(this.nodeAddress)
				.setServiceAddress(this.getNextServiceAddress())
				.build();
		
		// TODO Check to make sure the address space is for WORD data

		return this.send(new FinsFrame<>(header, new MemoryAreaReadCommand(address, itemCount)))
				.thenApply(f -> f.getPdu())
				.thenApply(MemoryAreaReadWordsResponse.class::cast)
				.thenApply(p -> p.getItems());
	}
	
	@Override
	public CompletableFuture<List<Word>> readWords(final FinsNodeAddress destination, final FinsIoAddress address, final int itemCount) {
		return readWords(destination, address, (short) itemCount);
	}

	@Override
	public CompletableFuture<Word> readWord(final FinsNodeAddress destination, final FinsIoAddress address) {
		return readWords(destination, address, 1).thenApply(words -> words.get(0));
	}

	@Override
	public CompletableFuture<byte[]> readBytes(final FinsNodeAddress destination, final FinsIoAddress address, final short itemCount) {
		return this.readWords(destination, address, (int) Math.ceil(itemCount / 2.0f)).thenApply(words -> {
			final byte[] bytes = new byte[itemCount];
			int i = 0;
			for (Word word : words) {
				final short s = word.getValue();
				bytes[i++] = (byte) ((s >> 8) & 0xff);
				bytes[i++] = (byte) (s & 0xff);
			}

			return bytes;
		});
	}
	
	@Override
	public CompletableFuture<byte[]> readBytes(FinsNodeAddress destination, FinsIoAddress address, int itemCount) {
		return readBytes(destination, address, (short) itemCount);
	}
	
	@Override
	public CompletableFuture<String> readString(final FinsNodeAddress destination, final FinsIoAddress address, final short length) {
		return this.readBytes(destination, address, length).thenApply(bytes -> {
			StringBuffer stringBuffer = new StringBuffer();
			try {
				stringBuffer.append(new String(bytes, "US-ASCII"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return stringBuffer.toString();
		});
	}
	
	

	@Override
	public CompletableFuture<String> readString(final FinsNodeAddress destination, final FinsIoAddress address, final int length) {
		return readString(destination, address, (short) length);
	}
	
//	@Override
//	public CompletableFuture<List<Bit>> readBits(final FinsNodeAddress destination, final FinsIoAddress address, final short itemCount) {
//		MemoryAreaReadCommand command = new MemoryAreaReadCommand(address, itemCount);
//
//		FinsFrame frame = new FinsFrameBuilder().setDestinationAddress(destination).setSourceAddress(this.nodeAddress)
//				.setServiceAddress(this.getNextServiceAddress()).setData(command.getBytes()).build();
//
//		FinsFrame replyFrame = this.send(frame);
//		byte[] data = replyFrame.getData();
//		MemoryAreaReadBitResponse response = MemoryAreaReadBitResponse.parseFrom(data, itemCount);
//		List<Bit> items = response.getItems();
//
//		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
//			throw new FinsMasterException(String.format("%s", response.getEndCode()));
//		}
//
//		return items;
//	}

//	@Override
//	public CompletableFuture<List<Bit>> readBits(final FinsNodeAddress destination, final FinsIoAddress address, final int itemCount) {
//		return readBits(destination, address, (short) itemCount);
//	}

//	@Override
//	public CompletableFuture<Bit> readBit(final FinsNodeAddress destination, final FinsIoAddress address) {
//		return readBits(destination, address, 1).handleAsync((bits, throwable) -> bits.get(0));
//	}

//	@Override
//	public CompletableFuture<List<Short>> readMultipleWords(final FinsNodeAddress destination, final List<FinsIoAddress> addresses) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("Not implemented yet");
//	}

	@Override
	public CompletableFuture<Void> writeWords(final FinsNodeAddress destination, final FinsIoAddress address, final List<Word> items) {	
		final FinsHeader header = FinsHeader.Builder.defaultCommandBuilder()
				.setDestinationAddress(destination)
				.setSourceAddress(this.nodeAddress)
				.setServiceAddress(this.getNextServiceAddress())
				.build();
		
		// TODO Check to make sure the address space is for WORD data

		return this.send(new FinsFrame<>(header, new MemoryAreaWriteWordsCommand(address, items)))
				.thenApply(f -> f.getPdu())
				.thenApply(SimpleResponse.class::cast)
				.thenApply(r -> null);
	}

	@Override
	public CompletableFuture<Void> writeWord(final FinsNodeAddress destination, final FinsIoAddress address, final Word value) {
		return writeWords(destination, address, Collections.singletonList(value));
	}

//	public CompletableFuture<Void> writeBit(final FinsNodeAddress destination, final FinsIoAddress address, final Boolean value) {
//		MemoryAreaWriteBitCommand command = new MemoryAreaWriteBitCommand(address, value);
//
//		FinsFrame frame = new FinsFrameBuilder().setDestinationAddress(destination).setSourceAddress(this.nodeAddress)
//				.setServiceAddress(this.getNextServiceAddress()).setData(command.getBytes()).build();
//
//		FinsFrame replyFrame = this.send(frame);
//		MemoryAreaWriteResponse response = MemoryAreaWriteResponse.parseFrom(replyFrame.getData());
//
//		if (response.getEndCode() != FinsEndCode.NORMAL_COMPLETION) {
//			throw new FinsMasterException(String.format("%s", response.getEndCode()));
//		}
//
//	}


	// Internal methods
	private <Command extends FinsCommand> CompletableFuture<FinsFrame<FinsResponse>> send(final FinsFrame<Command> frame, final int attempt) {
		logger.debug("Sending FinsFrame");
		final CompletableFuture<FinsFrame<FinsResponse>> future = new CompletableFuture<>();
		logger.debug("Storing response future with service ID {}", frame.getHeader().getServiceAddress());
		this.futures.put(frame.getHeader().getServiceAddress(), future);
		
		logger.debug("Writing and flushing FinsFrame");
		logger.debug("Channel {} Active:{} Writable:{} Open:{} Registered:{}", this.channel, this.channel.isActive(), this.channel.isWritable(), this.channel.isOpen(), this.channel.isRegistered());

		try {
			this.channel.writeAndFlush(frame).sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("Returning response future");

		return future;
//		try {
//
//			logger.debug("Write and flush FinsFrame");
//
//			logger.debug("Awaiting future to be completed");
//			FinsFrame replyFrame = this.sendFuture.get(1000, TimeUnit.MILLISECONDS);
//			logger.debug("Future compeleted");
//			return replyFrame;
//		} catch (TimeoutException e) {
//			if (attempt < this.retries) {
//				return send(frame, attempt++);
//			} else {
//				return null;
//			}
//		} catch (InterruptedException | ExecutionException e) {
//			return null;
//		}
	}

	private <Command extends FinsCommand> CompletableFuture<FinsFrame<FinsResponse>> send(final FinsFrame<Command> frame) {
		return this.send(frame, 0);
	}

	private synchronized byte getNextServiceAddress() {
		return (byte) this.serviceAddress.incrementAndGet();
	}

}
