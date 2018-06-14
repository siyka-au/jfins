package com.siyka.omron.fins.codec;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsIoMemoryArea;
import com.siyka.omron.fins.commands.FinsAddressableCommand;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteBitCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteDoubleWordCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteWordCommand;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.responses.FinsSimpleResponse;
import com.siyka.omron.fins.responses.MemoryAreaReadResponse;
import com.siyka.omron.fins.responses.MemoryAreaReadWordResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Timeout;

public class FinsFrameUdpMasterCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame> {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final InetSocketAddress destinationSocketAddress;
	private final InetSocketAddress sourceSocketAddress;

	private final Map<Short, PendingCommand<? extends FinsResponse>> pendingCommands;
	
	public FinsFrameUdpMasterCodec(final InetSocketAddress destinationSocketAddress, final InetSocketAddress sourceSocketAddress) {
		super();
		Objects.requireNonNull(destinationSocketAddress);
		Objects.requireNonNull(sourceSocketAddress);
		this.destinationSocketAddress = destinationSocketAddress;
		this.sourceSocketAddress = sourceSocketAddress;
		this.pendingCommands = new HashMap<>();
	}
	
	public static class PendingCommand<Response extends FinsResponse> {
		
		private final FinsFrame frame;
		private final CompletableFuture<FinsResponse> promise = new CompletableFuture<>();
		
		public PendingCommand(final FinsFrame frame, final CompletableFuture<Response> future) {
			this.frame = frame;
			this.promise.whenComplete((response, exception) -> {
				if (response != null) {
					try {
						future.complete((Response) response);
					} catch (ClassCastException castException) {
						future.completeExceptionally(castException);
					}
				} else {
					future.completeExceptionally(exception);
				}
			});
		}
	}
	
	/*
	 * Encoder section
	 * 
	 */
	
	@Override
	protected void encode(final ChannelHandlerContext context, final FinsFrame frame, final List<Object> out) {
		final ByteBuf buffer = Unpooled.buffer();
//		final FinsFrame frame = pendingCommand.frame;
		
		buffer.writeBytes(FinsHeaderCodec.encode(frame.getHeader()));
		buffer.writeShort(frame.getPdu().getCommandCode().getValue());
		
		if (frame.getPdu() instanceof FinsAddressableCommand) {
			buffer.writeByte(((FinsAddressableCommand) frame.getPdu()).getIoAddress().getMemoryArea().getValue());
			buffer.writeShort(((FinsAddressableCommand) frame.getPdu()).getIoAddress().getAddress());
			buffer.writeByte(((FinsAddressableCommand) frame.getPdu()).getIoAddress().getBitOffset());
		}
		
		switch (frame.getPdu().getCommandCode()) {
			case MEMORY_AREA_READ:
				encodeMemoryAreaReadCommand((MemoryAreaReadCommand) frame.getPdu(), buffer);
				break;
					
			case MEMORY_AREA_WRITE:
				encodeMemoryAreaWriteCommand((MemoryAreaWriteCommand<?>) frame.getPdu(), buffer);
				break;
				
			default:
				throw new EncoderException(String.format("Command code not implemented or supported: %s", frame.getPdu().getCommandCode()));
		}

		DatagramPacket packet = new DatagramPacket(buffer, this.destinationSocketAddress, this.sourceSocketAddress);
		out.add(packet);
	}
	
	private void encodeMemoryAreaReadCommand(final MemoryAreaReadCommand command, final ByteBuf buffer) {
		buffer.writeShort(command.getItemCount());
	}
	
	@SuppressWarnings("unchecked")
	private void encodeMemoryAreaWriteCommand(final MemoryAreaWriteCommand<?> command, final ByteBuf buffer) {
		buffer.writeShort(command.getItems().size());
		if (command instanceof MemoryAreaWriteBitCommand) {
			encodeMemoryAreaWriteBitCommand((MemoryAreaWriteCommand<Bit>) command, buffer);
		} else if (command instanceof MemoryAreaWriteWordCommand) {
			encodeMemoryAreaWriteWordCommand((MemoryAreaWriteCommand<Short>) command, buffer);
		} else if (command instanceof MemoryAreaWriteDoubleWordCommand) {
			encodeMemoryAreaWriteDoubleWordCommand((MemoryAreaWriteCommand<Integer>) command, buffer);
		}
	}
	
	private void encodeMemoryAreaWriteBitCommand(final MemoryAreaWriteCommand<Bit> command, final ByteBuf buffer) {
//		command.getItems().forEach(i -> buffer.writeByte(i));
	}
	
	private void encodeMemoryAreaWriteWordCommand(final MemoryAreaWriteCommand<Short> command, final ByteBuf buffer) {
		command.getItems().forEach(i -> buffer.writeShort(i));
	}
	
	private void encodeMemoryAreaWriteDoubleWordCommand(final MemoryAreaWriteCommand<Integer> command, final ByteBuf buffer) {
		command.getItems().forEach(i -> buffer.writeInt(i));
	}
	
	/*
	 * Decoder section
	 * 
	 */
	
	@Override
	protected void decode(final ChannelHandlerContext context, final DatagramPacket packet, final List<Object> out) {
		final ByteBuf buffer = packet.content();
		final FinsHeader header = FinsHeaderCodec.decode(buffer);
		
		final short commandCodeRaw = buffer.readShort();
		final FinsCommandCode commandCode = FinsCommandCode.valueOf(commandCodeRaw)
				.orElseThrow(() -> new DecoderException(String.format("Unrecognised command code 0x%0x", commandCodeRaw)));
		
		final short endCodeRaw = buffer.readShort();
		final FinsEndCode endCode = FinsEndCode.valueOf(endCodeRaw)
				.orElseThrow(() -> new DecoderException(String.format("Unrecognised end code 0x%0x", endCodeRaw)));
		
		switch (commandCode) {
			case MEMORY_AREA_READ:
				frame = new FinsFrame(header, decodeMemoryAreaReadResponse(commandCode, endCode, buffer));
				break;
		
			case MEMORY_AREA_WRITE:
			case MEMORY_AREA_FILL:
			case MEMORY_AREA_TRANSFER:
			case PARAMETER_AREA_WRITE:
			case PROGRAM_AREA_CLEAR:
			case RUN:
			case STOP:
				frame = new FinsFrame(header, new FinsSimpleResponse(commandCode, endCode));
				break;

			default:
				throw new DecoderException(String.format("Command code not implemented or supported: %s", commandCode));
		}

		out.add(frame);
	}
	
	private FinsIoAddress decodeIoAddress(final ByteBuf buffer) {
		final byte ioMemoryAreaCode = buffer.readByte();
		final FinsIoMemoryArea memoryAreaCode =  FinsIoMemoryArea.valueOf(ioMemoryAreaCode)
				.orElseThrow(() -> new DecoderException(String.format("Unrecognised IO memory area code 0x%x", ioMemoryAreaCode)));
		final short address = buffer.readShort();
		final byte bitOffset = buffer.readByte();
		return new FinsIoAddress(memoryAreaCode, address, bitOffset);
	}
	
	private MemoryAreaReadResponse<?> decodeMemoryAreaReadResponse(final FinsCommandCode commandCode, final FinsEndCode endCode, final ByteBuf buffer) {
		final short itemCount = buffer.readShort();
		final List<Short> items = new ArrayList<>();

//		switch (ioAddress.getMemoryArea().getDataType()) {
//			case WORD:
				IntStream.range(0, itemCount)
						.forEach(i -> items.add(buffer.readShort()));
				return new MemoryAreaReadWordResponse(commandCode, endCode, items);
				
//			default:
//		}
		
//		return null;
	}

}
