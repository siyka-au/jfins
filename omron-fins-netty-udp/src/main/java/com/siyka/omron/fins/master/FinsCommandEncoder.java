package com.siyka.omron.fins.master;

import com.siyka.omron.fins.FinsPdu;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteBitsCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteWordsCommand;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;

public class FinsCommandEncoder implements FinsPduEncoder {

	@Override
	public ByteBuf encode(FinsPdu pdu, ByteBuf buffer) {
		try {

			final FinsCommand command = (FinsCommand) pdu;
//			this.outgoingCommands.put(frame.getHeader().getServiceAddress(), command);

			if (command instanceof MemoryAreaReadCommand) {
				return encodeMemoryAreaReadCommand(buffer, (MemoryAreaReadCommand) command);
			} else if (command instanceof MemoryAreaWriteWordsCommand) {
				return encodeMemoryAreaWriteWordsCommand(buffer, (MemoryAreaWriteWordsCommand) command);
			} else if (command instanceof MemoryAreaWriteBitsCommand) {
				return encodeMemoryAreaWriteBitsCommand(buffer, (MemoryAreaWriteBitsCommand) command);
			}
			
			throw new EncoderException("CommandCode not supported: " + command.getCommandCode());
		} finally {
			ReferenceCountUtil.release(pdu);
		}
	}

	ByteBuf encodeMemoryAreaReadCommand(final ByteBuf buffer, final MemoryAreaReadCommand command)  {
		Codecs.encodeCommandCode(buffer, command.getCommandCode());
		Codecs.encodeIoAddress(buffer, command.getAddress());
		buffer.writeShort(command.getItemCount());
		return buffer;
	}
	
	ByteBuf encodeMemoryAreaWriteWordsCommand(final ByteBuf buffer, final MemoryAreaWriteWordsCommand command) {
		Codecs.encodeCommandCode(buffer, command.getCommandCode());
		Codecs.encodeIoAddress(buffer, command.getAddress());
		buffer.writeShort(command.getItems().size());
		command.getItems().forEach(word -> buffer.writeShort(word.getValue()));
		return buffer;
	}
	
	ByteBuf encodeMemoryAreaWriteBitsCommand(final ByteBuf buffer, final MemoryAreaWriteBitsCommand command) {
		Codecs.encodeCommandCode(buffer, command.getCommandCode());
		Codecs.encodeIoAddress(buffer, command.getAddress());
		buffer.writeShort(command.getItems().size());
		command.getItems().forEach(bit -> buffer.writeByte(bit.getValue() ? 0x01 : 0x00));
		return buffer;
	}
	
}
