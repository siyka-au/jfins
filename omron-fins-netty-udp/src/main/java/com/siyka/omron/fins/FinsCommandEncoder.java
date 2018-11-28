package com.siyka.omron.fins;

import static com.siyka.omron.fins.CommonCodecs.encodeCommandCode;
import static com.siyka.omron.fins.CommonCodecs.encodeIoAddress;

import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;

public class FinsCommandEncoder implements FinsPduEncoder<FinsCommand> {

	@Override
	public ByteBuf encode(FinsCommand command, ByteBuf buffer) {
		try {
			encodeCommandCode(command.getCommandCode(), buffer);
			
			switch (command.getCommandCode()) {
				case MEMORY_AREA_READ:
					return encodeMemoryAreaReadCommand((MemoryAreaReadCommand) command, buffer);
					
				case MEMORY_AREA_WRITE:
					return encodeMemoryAreaWriteCommand((MemoryAreaWriteCommand) command, buffer);
					
				default:
					throw new EncoderException("CommandCode not supported: " + command.getCommandCode());
			}			
		} finally {
			ReferenceCountUtil.release(command);
		}
	}

	ByteBuf encodeMemoryAreaReadCommand(MemoryAreaReadCommand command, ByteBuf buffer)  {
		encodeIoAddress(command.getAddress() , buffer);
		buffer.writeShort(command.getItemCount());
		return buffer;
	}
	
	ByteBuf encodeMemoryAreaWriteCommand(MemoryAreaWriteCommand command, ByteBuf buffer) {
		encodeIoAddress(command.getAddress(), buffer);
		buffer.writeShort(command.getData().length / 2);
		buffer.writeBytes(command.getData());
		return buffer;
	}
	
}
