package com.siyka.omron.fins.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.commands.FinsAddressableCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteBitCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteDoubleWordCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteWordCommand;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;

public class FinsCommandFrameEncoder implements FinsFrameEncoder {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public ByteBuf encode(final FinsFrame frame) {
		final ByteBuf buffer = Unpooled.buffer();
		
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

		return buffer;
	}

	private void encodeMemoryAreaReadCommand(final MemoryAreaReadCommand command, final ByteBuf buffer) {
		buffer.writeShort(command.getItemCount());
	}
	
	@SuppressWarnings("unchecked")
	private void encodeMemoryAreaWriteCommand(final MemoryAreaWriteCommand<?> command, final ByteBuf buffer) {
		buffer.writeShort(command.getItems().size());
		if (command instanceof MemoryAreaWriteBitCommand) {
//			encodeMemoryAreaWriteBitCommand((MemoryAreaWriteCommand<Bit>) frame.getPdu(), buffer);
		} else if (command instanceof MemoryAreaWriteWordCommand) {
			encodeMemoryAreaWriteWordCommand((MemoryAreaWriteCommand<Short>) command, buffer);
		} else if (command instanceof MemoryAreaWriteDoubleWordCommand) {
			encodeMemoryAreaWriteDoubleWordCommand((MemoryAreaWriteCommand<Integer>) command, buffer);
		}
	}
	
	private void encodeMemoryAreaWriteWordCommand(final MemoryAreaWriteCommand<Short> command, final ByteBuf buffer) {
		command.getItems().forEach(i -> buffer.writeShort(i));
	}
	
	private void encodeMemoryAreaWriteDoubleWordCommand(final MemoryAreaWriteCommand<Integer> command, final ByteBuf buffer) {
		command.getItems().forEach(i -> buffer.writeInt(i));
	}

}
