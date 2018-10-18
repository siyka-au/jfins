package com.siyka.omron.fins.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;

public class FinsCommandFrameEncoder implements FinsFrameEncoder {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public ByteBuf encode(final FinsFrame frame) {
		try {
            switch (frame.getPdu().getCommandCode()) {
                case MEMORY_AREA_WRITE:
                    return null;
                    
                default:
                    throw new EncoderException("FunctionCode not supported: " + frame.getPdu().getCommandCode());
            }
        } finally {
            ReferenceCountUtil.release(frame);
        }
	}

}
