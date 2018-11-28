package com.siyka.omron.fins;

import static com.siyka.omron.fins.CommonCodecs.decodeCommandCode;
import static com.siyka.omron.fins.CommonCodecs.decodeResponseCode;

import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.responses.MemoryAreaReadResponse;
import com.siyka.omron.fins.responses.SimpleResponse;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public class FinsResponseDecoder implements FinsPduDecoder<FinsResponse> {

	@Override
	public FinsResponse decode(ByteBuf buffer) throws DecoderException {
		final CommandCode commandCode = decodeCommandCode(buffer);
		final ResponseCode endCode = decodeResponseCode(buffer);
		
		switch (commandCode) {
			case MEMORY_AREA_READ:
				return decodeMemoryAreaReadResponse(endCode, buffer);

			case MEMORY_AREA_WRITE:
				return new SimpleResponse(commandCode, endCode);
	
			default:
				throw new DecoderException("CommandCode not supported: " + commandCode);
		}
	}

	MemoryAreaReadResponse decodeMemoryAreaReadResponse(ResponseCode endCode, ByteBuf buffer) {
		byte[] bytes = new byte[buffer.readableBytes()];
		buffer.readBytes(bytes);
		return new MemoryAreaReadResponse(endCode, bytes);
	};

}
