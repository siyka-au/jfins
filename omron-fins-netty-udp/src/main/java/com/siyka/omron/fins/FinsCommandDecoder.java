package com.siyka.omron.fins;

import static com.siyka.omron.fins.CommonCodecs.decodeCommandCode;
import static com.siyka.omron.fins.CommonCodecs.decodeIoAddress;

import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public class FinsCommandDecoder implements FinsPduDecoder<FinsCommand> {

	@Override
	public FinsCommand decode(ByteBuf buffer) throws DecoderException {
		final CommandCode commandCode = decodeCommandCode(buffer);
		
		switch (commandCode) {
			case MEMORY_AREA_WRITE:
				return decodeMemoryAreaWriteCommand(buffer);
	
			default:
				throw new DecoderException("CommandCode not supported: " + commandCode);
		}
	}

	MemoryAreaWriteCommand decodeMemoryAreaWriteCommand(ByteBuf buffer) {
		FinsIoAddress address = decodeIoAddress(buffer);
		int itemCount = buffer.readShort();
		byte[] bytes = new byte[buffer.readableBytes()];
		buffer.readBytes(bytes);
		return new MemoryAreaWriteCommand(address, bytes);
	};

}
