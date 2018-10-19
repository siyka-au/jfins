package com.siyka.omron.fins.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.wip.MemoryAreaReadCommand;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;

public class FinsCommandFrameEncoder implements FinsFrameEncoder<FinsCommand> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final FinsHeaderCodec headerCodec = new FinsHeaderCodec();
	
	@Override
	public ByteBuf encode(final FinsFrame<FinsCommand> frame) {
		logger.debug("Encoding FINS command frame");
		try {
			final ByteBuf buffer = Unpooled.buffer();
			
			headerCodec.encode(buffer, frame.getHeader());
			
			buffer.writeShort(frame.getPdu().getCommandCode().getValue());
            switch (frame.getPdu().getCommandCode()) {
                case MEMORY_AREA_READ:
                	logger.debug("Encoding FINS MEMORY_AREA_READ command frame");
                	if (frame.getPdu() instanceof MemoryAreaReadCommand) {
                		logger.debug("Encoding FINS MemoryAreaReadCommand");
                		encodeMemoryAreaReadCommand(buffer, (MemoryAreaReadCommand) frame.getPdu());
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

	private void encodeMemoryAreaReadCommand(final ByteBuf buffer, final MemoryAreaReadCommand command) {
		buffer.writeByte(command.getIoAddress().getMemoryArea().getValue());
		buffer.writeShort(command.getIoAddress().getAddress());
		buffer.writeByte(command.getIoAddress().getBitOffset());
		buffer.writeShort(command.getItemCount());
	}

}
