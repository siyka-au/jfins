package com.siyka.omron.fins.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.responses.FinsResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

public class FinsResponseFrameEncoder implements FinsFrameEncoder<FinsResponse> {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final FinsHeaderCodec headerCodec = new FinsHeaderCodec();
	
	@Override
	public ByteBuf encode(final FinsFrame<FinsResponse> response) {
		logger.debug("Encoding FINS response frame");
		try {
			ByteBuf buffer = Unpooled.buffer();
			headerCodec.encode(buffer, response.getHeader());
			
			buffer.writeShort(response.getPdu().getCommandCode().getValue());

			buffer.writeShort(response.getPdu().getEndCode().getValue());
			
			return buffer;
		} finally {
			ReferenceCountUtil.release(response);
		}
	}

}
