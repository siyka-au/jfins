package com.siyka.omron.fins.codec;

import com.siyka.omron.fins.FinsFrame;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public interface FinsFrameDecoder {

	public FinsFrame decode(final ByteBuf buffer) throws DecoderException;
	
}
