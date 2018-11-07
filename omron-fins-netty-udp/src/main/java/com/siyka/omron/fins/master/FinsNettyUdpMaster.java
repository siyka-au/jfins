package com.siyka.omron.fins.master;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsCommand;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsNode;
import com.siyka.omron.fins.FinsResponse;
import com.siyka.omron.fins.MemoryAreaReadCommand;
import com.siyka.omron.fins.MemoryAreaReadWordsResponse;
import com.siyka.omron.fins.MemoryAreaWriteBitsCommand;
import com.siyka.omron.fins.MemoryAreaWriteWordsCommand;
import com.siyka.omron.fins.SimpleResponse;
import com.siyka.omron.fins.Word;

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

	private final FinsNode remote;
	private final FinsNode local;
	
	private NioEventLoopGroup workerGroup;
	private Bootstrap bootstrap;
	private Channel channel;

	private final AtomicInteger serviceAddress = new AtomicInteger(0);

	private final Map<Byte, CompletableFuture<FinsFrame<FinsResponse>>> futures;

	// TODO make configurable
//	private int retries = 3;

	public FinsNettyUdpMaster(final FinsNode remote, final FinsNode local) {
		this.remote = remote;
		this.local = local;

		futures = new HashMap<>();

		workerGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup)
				.channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.remoteAddress(remote.getSocketAddress())
				.localAddress(local.getSocketAddress())
				.handler(new ChannelInitializer<DatagramChannel>() {
					@Override
					public void initChannel(DatagramChannel channel) {
						channel.pipeline()
								.addLast(new LoggingHandler(LogLevel.DEBUG))
								.addLast(new FinsFrameUdpCodec(
										remote.getSocketAddress(),
										local.getSocketAddress()))
								.addLast(new FinsMasterHandler(futures));
					}
				});
	}

	// FINS Master API
	@Override
	public CompletableFuture<Void> connect() {
		return CompletableFuture.runAsync(() -> {
			try {
				channel = bootstrap.connect().sync().channel();
			} catch (InterruptedException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> disconnect() {
		return CompletableFuture.runAsync(() -> {
			try {
//				channel.closeFuture().sync();
				workerGroup.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				throw new CompletionException(e);
			}
		});
	}
	
	@Override
	public CompletableFuture<List<Word>> readWords(final FinsIoAddress address, final int itemCount) {
	
		// TODO Check to make sure the address space is for WORD data

		return send(new FinsFrame<>(defaultCommandHeader(), new MemoryAreaReadCommand(address, itemCount)))
				.thenApply(f -> f.getPdu())
				.thenApply(MemoryAreaReadWordsResponse.class::cast)
				.thenApply(p -> p.getItems());
	}

	@Override
	public CompletableFuture<Word> readWord(final FinsIoAddress address) {
		return readWords(address, 1).thenApply(words -> words.get(0));
	}

	@Override
	public CompletableFuture<byte[]> readBytes(final FinsIoAddress address, final int itemCount) {
		return readWords(address, (int) Math.ceil(itemCount / 2.0f)).thenApply(words -> {
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
	public CompletableFuture<String> readString(final FinsIoAddress address, final int length) {
		return readBytes(address, length).thenApply(bytes -> {
			int nullTerminatorIndex = bytes.length;
			for (int i = 0; i < bytes.length && nullTerminatorIndex == bytes.length; i++)
				if (bytes[i] == 0x00)
					nullTerminatorIndex = i;
				
			try {
				return new String(bytes, 0, nullTerminatorIndex, "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		});
	}
	
//	@Override
//	public CompletableFuture<List<Bit>> readBits(final FinsIoAddress address, final short itemCount) {
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
//	public CompletableFuture<List<Bit>> readBits(final FinsIoAddress address, final int itemCount) {
//		return readBits(destination, address, (short) itemCount);
//	}

//	@Override
//	public CompletableFuture<Bit> readBit(final FinsIoAddress address) {
//		return readBits(destination, address, 1).handleAsync((bits, throwable) -> bits.get(0));
//	}

//	@Override
//	public CompletableFuture<List<Short>> readMultipleWords(final List<FinsIoAddress> addresses) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("Not implemented yet");
//	}
	
	@Override
	public CompletableFuture<Void> writeWords(final FinsIoAddress address, final List<Word> items) {	

		// TODO Check to make sure the address space is for WORD data

		return send(new FinsFrame<>(defaultCommandHeader(), new MemoryAreaWriteWordsCommand(address, items)))
				.thenApply(f -> f.getPdu())
				.thenApply(SimpleResponse.class::cast)
				.thenApply(r -> null);
	}

	@Override
	public CompletableFuture<Void> writeWords(final FinsIoAddress address, final Word... items) {	
		return writeWords(address, Arrays.asList(items)); 
	}
	
	@Override
	public CompletableFuture<Void> writeWord(final FinsIoAddress address, final Word value) {
		return writeWords(address, Collections.singletonList(value));
	}

	@Override
	public CompletableFuture<Void> writeBits(final FinsIoAddress address, final List<Bit> items) {

		// TODO Check to make sure the address space is for WORD data

		return send(new FinsFrame<>(defaultCommandHeader(), new MemoryAreaWriteBitsCommand(address, items)))
				.thenApply(f -> f.getPdu())
				.thenApply(SimpleResponse.class::cast)
				.thenApply(r -> null);
	}
	
	@Override
	public CompletableFuture<Void> writeBits(final FinsIoAddress address, final Bit... items) {
		return writeBits(address, Arrays.asList(items));
	}
	
	@Override
	public CompletableFuture<Void> writeBit(final FinsIoAddress address, final Bit value) {
		return writeBits(address, Collections.singletonList(value));
	}
	

	@Override
	public CompletableFuture<Void> writeBytes(final FinsIoAddress address, final byte... bytes) {
		final int wordLength = (int) Math.ceil(bytes.length / 2.0f);
		final List<Word> words = new ArrayList<>(wordLength);
		for (int i = 0; i < bytes.length; i += 2) {
			short value = (short) (bytes[i] << 8);
			if (i + 1 < bytes.length) {
				value |= (short) bytes[i + 1];
			}
			words.add(new Word(value));
		}

		return writeWords(address, words);
	}
	
	@Override
	public CompletableFuture<Void> writeBytes(final FinsIoAddress address, final Byte... byteObjects) {
		return writeBytes(address, Arrays.asList(byteObjects));
	}
	
	@Override
	public CompletableFuture<Void> writeBytes(final FinsIoAddress address, final List<Byte> byteObjects) {
		byte[] bytes = new byte[byteObjects.size()];

		int i = 0;
		for(Byte b: byteObjects)
		    bytes[i++] = b.byteValue();
		
		return writeBytes(address, bytes);
	}

	@Override
	public CompletableFuture<Void> writeString(final FinsIoAddress address, final String text, final int length) {
		byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
		if (bytes[bytes.length - 1] != 0) {
			byte[] nullTerminatedBytes = new byte[bytes.length + 1];
			System.arraycopy(bytes, 0, nullTerminatedBytes, 0, bytes.length);
			bytes = nullTerminatedBytes;
		}
		return writeBytes(address, bytes);
	}
	
	@Override
	public CompletableFuture<Void> writeString(final FinsIoAddress address, final String text) {
		byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
		if (bytes[bytes.length - 1] != 0) {
			byte[] nullTerminatedBytes = new byte[bytes.length + 1];
			System.arraycopy(bytes, 0, nullTerminatedBytes, 0, bytes.length);
			bytes = nullTerminatedBytes;
		}
		return writeBytes(address, bytes);
	}
	
	// Internal methods
	private <Command extends FinsCommand> CompletableFuture<FinsFrame<FinsResponse>> send(final FinsFrame<Command> frame, final int attempt) {
		logger.debug("Sending FinsFrame");
		final CompletableFuture<FinsFrame<FinsResponse>> future = new CompletableFuture<>();
		logger.debug("Storing response future with service ID {}", frame.getHeader().getServiceAddress());
		futures.put(frame.getHeader().getServiceAddress(), future);
		
		logger.debug("Writing and flushing FinsFrame");
		logger.debug("Channel {} Active:{} Writable:{} Open:{} Registered:{}", this.channel, this.channel.isActive(), this.channel.isWritable(), this.channel.isOpen(), this.channel.isRegistered());

		try {
			channel.writeAndFlush(frame).sync();
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
		return send(frame, 0);
	}

	private FinsHeader defaultCommandHeader() {
		return FinsHeader.Builder.defaultCommandBuilder()
				.setDestinationAddress(remote.getNodeAddress())
				.setSourceAddress(local.getNodeAddress())
				.setServiceAddress(getNextServiceAddress())
				.build();
	}
	
	private synchronized byte getNextServiceAddress() {
		return (byte) serviceAddress.incrementAndGet();
	}

}
