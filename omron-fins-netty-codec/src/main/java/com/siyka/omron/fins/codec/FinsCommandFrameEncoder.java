package com.siyka.omron.fins.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;

public class FinsCommandFrameEncoder implements FinsFrameEncoder<FinsCommand> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public ByteBuf encode(final FinsFrame frame) {
		logger.debug("Encoding FINS command frame");
		try {
			final ByteBuf buffer = Unpooled.buffer();
			
			FinsHeaderCodec.encoder.encode(frame.getHeader());
			
            switch (frame.getPdu().getCommandCode()) {
                case MEMORY_AREA_READ:
                	logger.debug("Encoding FINS MEMORY_AREA_READ command frame");
                	if (frame.getPdu() instanceof MemoryAreaReadCommand) {
                		logger.debug("Encoding FINS MemoryAreaReadCommand");
//                		encodeMemoryAreaReadCommand(buffer, (MemoryAreaReadCommand) frame.getPdu());
                	}
                    break;
                    
                default:
                    throw new EncoderException("FunctionCode not supported: " + frame.getPdu().getCommandCode());
            }
            return buffer;
        } finally {
            ReferenceCountUtil.release(frame);
        }
	}

}
