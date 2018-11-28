package com.siyka.omron.fins;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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

import com.siyka.omron.fins.FinsHeader.MessageType;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.master.FinsMaster;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.responses.MemoryAreaReadResponse;
import com.siyka.omron.fins.responses.SimpleResponse;
import com.siyka.omron.fins.slave.FinsSlave;
import com.siyka.omron.fins.slave.ServiceCommandHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FinsNettyUdp implements FinsMaster, FinsSlave {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private FinsNode remote;
	private FinsNode local;
	
	private NioEventLoopGroup workerGroup;
	private Bootstrap bootstrap;
	private Channel channel;

	private AtomicInteger serviceAddress = new AtomicInteger(0);

	private Map<Byte, CompletableFuture<FinsResponse>> responseFutures;

	private ServiceCommandHandler handler;

	// TODO make configurable
//	private int retries = 3;

	public FinsNettyUdp(FinsNode remote, FinsNode local) {
		this.remote = remote;
		this.local = local;

		responseFutures = new HashMap<>();

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
								.addLast(new FinsMasterHandler(responseFutures))
								.addLast(new FinsSlaveHandler(FinsNettyUdp.this));
					}
				});
	}

	public CompletableFuture<Void> connect() {
		return CompletableFuture.runAsync(() -> {
			try {
				channel = bootstrap.connect().sync().channel();
			} catch (InterruptedException e) {
				throw new CompletionException(e);
			}
		});
	}

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

	// FINS Master API
	@Override
	public CompletableFuture<byte[]> readBytes(FinsIoAddress address, int itemCount) {
		return send(new MemoryAreaReadCommand(address, itemCount))
				.thenApply(MemoryAreaReadResponse.class::cast)
				.thenApply(response -> response.getData());
	}
	
	@Override
	public CompletableFuture<List<Word>> readWords(FinsIoAddress address, int itemCount) {
	
		// TODO Check to make sure the address space is for WORD data

		return readBytes(address, itemCount)
				.thenApply(bytes -> {
					return null;
				});
	}

	@Override
	public CompletableFuture<Word> readWord(FinsIoAddress address) {
		return readWords(address, 1).thenApply(words -> words.get(0));
	}
	
	@Override
	public CompletableFuture<String> readString(FinsIoAddress address, int length) {
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
//	public CompletableFuture<List<Bit>> readBits(FinsIoAddress address, short itemCount) {
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
//	public CompletableFuture<List<Bit>> readBits(FinsIoAddress address, int itemCount) {
//		return readBits(destination, address, (short) itemCount);
//	}

//	@Override
//	public CompletableFuture<Bit> readBit(FinsIoAddress address) {
//		return readBits(destination, address, 1).handleAsync((bits, throwable) -> bits.get(0));
//	}

//	@Override
//	public CompletableFuture<List<Short>> readMultipleWords(List<FinsIoAddress> addresses) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("Not implemented yet");
//	}
	
	@Override
	public CompletableFuture<Void> writeBytes(FinsIoAddress address, byte... bytes) {
		return send(new MemoryAreaWriteCommand(address, bytes))
				.thenApply(SimpleResponse.class::cast)
				.thenApply(r -> null);
	}
	
	@Override
	public CompletableFuture<Void> writeBytes(FinsIoAddress address, Byte... byteObjects) {
		return writeBytes(address, Arrays.asList(byteObjects));
	}
	
	@Override
	public CompletableFuture<Void> writeBytes(FinsIoAddress address, List<Byte> byteObjects) {
		byte[] bytes = new byte[byteObjects.size()];

		int i = 0;
		for(Byte b: byteObjects)
		    bytes[i++] = b.byteValue();
		
		return writeBytes(address, bytes);
	}
	
	@Override
	public CompletableFuture<Void> writeWords(FinsIoAddress address, List<Word> items) {	

		// TODO Check to make sure the address space is for WORD data

		byte[] bytes = new byte[items.size() * 2];
		
		ByteBuf buffer = Unpooled.buffer();
		
		items.forEach(item -> buffer.writeShort(item.getValue()));
		buffer.readBytes(bytes);
		
		return writeBytes(address, bytes);
	}

	@Override
	public CompletableFuture<Void> writeWords(FinsIoAddress address, Word... items) {	
		return writeWords(address, Arrays.asList(items)); 
	}
	
	@Override
	public CompletableFuture<Void> writeWord(FinsIoAddress address, Word value) {
		return writeWords(address, Collections.singletonList(value));
	}

	@Override
	public CompletableFuture<Void> writeBits(FinsIoAddress address, List<Bit> items) {

		// TODO Check to make sure the address space is for WORD data

		byte[] bytes = new byte[items.size()];
		
		for (int i = 0; i < items.size(); i++) {
			bytes[i] = (byte) (items.get(i).getValue() ? 0x01 : 0x00);
		}
		
		return writeBytes(address, bytes);
	}
	
	@Override
	public CompletableFuture<Void> writeBits(FinsIoAddress address, Bit... items) {
		return writeBits(address, Arrays.asList(items));
	}
	
	@Override
	public CompletableFuture<Void> writeBit(FinsIoAddress address, Bit value) {
		return writeBits(address, Collections.singletonList(value));
	}

	@Override
	public CompletableFuture<Void> writeString(FinsIoAddress address, String text, int length) {
		byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
		if (bytes[bytes.length - 1] != 0) {
			byte[] nullTerminatedBytes = new byte[bytes.length + 1];
			System.arraycopy(bytes, 0, nullTerminatedBytes, 0, bytes.length);
			bytes = nullTerminatedBytes;
		}
		return writeBytes(address, bytes);
	}
	
	@Override
	public CompletableFuture<Void> writeString(FinsIoAddress address, String text) {
		byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
		if (bytes[bytes.length - 1] != 0) {
			byte[] nullTerminatedBytes = new byte[bytes.length + 1];
			System.arraycopy(bytes, 0, nullTerminatedBytes, 0, bytes.length);
			bytes = nullTerminatedBytes;
		}
		return writeBytes(address, bytes);
	}
	
	// FINS Slave API
	@Override
	public void setHandler(ServiceCommandHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public ServiceCommandHandler getHandler() {
		return handler;
	}
	
	// Internal methods
	private <C extends FinsCommand> CompletableFuture<FinsResponse> send(C command, int attempt) {
		logger.debug("Sending FinsFrame");
		CompletableFuture<FinsResponse> future = new CompletableFuture<>();
		FinsFrame<C> frame = new FinsFrame<>(defaultCommandHeader(), command);
		logger.debug("Storing response future with service ID {}", frame.getHeader().getServiceAddress());
		responseFutures.put(frame.getHeader().getServiceAddress(), future);
		
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
	}

	private <C extends FinsCommand> CompletableFuture<FinsResponse> send(C command) {
		return send(command, 0);
	}
	
	<R extends FinsResponse> CompletableFuture<Void> send(R response) {
		return CompletableFuture.runAsync(() -> {
			try {
				FinsFrame<R> frame = new FinsFrame<>(defaultCommandHeader(), response);
				channel.writeAndFlush(frame).sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private FinsHeader defaultCommandHeader() {
		return FinsHeader.Builder.defaultCommandBuilder()
				.setDestinationAddress(remote.getNodeAddress())
				.setSourceAddress(local.getNodeAddress())
				.setServiceAddress(getNextServiceAddress())
				.build();
	}
	
	private FinsHeader defaultResponseHeader(FinsFrame<FinsCommand> frame) {
		return FinsHeader.Builder.defaultCommandBuilder()
				.setDestinationAddress(frame.getHeader().getSourceAddress())
				.setSourceAddress(frame.getHeader().getDestinationAddress())
				.setServiceAddress(frame.getHeader().getServiceAddress())
				.setMessageType(MessageType.RESPONSE)
				.build();
	}
	
	private synchronized byte getNextServiceAddress() {
		return (byte) serviceAddress.incrementAndGet();
	}

}
