package com.siyka.omron.fins;

import static com.siyka.omron.fins.CommonCodecs.encodeCommandCode;
import static com.siyka.omron.fins.CommonCodecs.encodeResponseCode;

import com.siyka.omron.fins.responses.FinsResponse;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;

public class FinsResponseEncoder implements FinsPduEncoder<FinsResponse> {

	@Override
	public ByteBuf encode(FinsResponse response, ByteBuf buffer) {
		try {
			encodeCommandCode(response.getCommandCode(), buffer);
			encodeResponseCode(response.getResponseCode(), buffer);
			
			switch (response.getCommandCode()) {
				case MEMORY_AREA_WRITE:
					return buffer;
					
				default:
					throw new EncoderException("CommandCode not supported: " + response.getCommandCode());
			}			
		} finally {
			ReferenceCountUtil.release(response);
		}
	}

}
