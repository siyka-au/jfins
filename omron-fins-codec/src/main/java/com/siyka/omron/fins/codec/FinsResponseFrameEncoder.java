package com.siyka.omron.fins.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.responses.FinsResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

public class FinsResponseFrameEncoder implements FinsFrameEncoder {
	
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public ByteBuf encode(final FinsFrame response) {
		try {
			ByteBuf buffer = Unpooled.buffer();
			buffer.writeBytes(FinsHeaderCodec.encode(response.getHeader()));
			buffer.writeShort(response.getPdu().getCommandCode().getValue());
			
			if (response instanceof FinsResponse) {
				buffer.writeShort(((FinsResponse) response).getEndCode().getValue());
			}
			return buffer;
		} finally {
			ReferenceCountUtil.release(response);
		}
	}
	
}
