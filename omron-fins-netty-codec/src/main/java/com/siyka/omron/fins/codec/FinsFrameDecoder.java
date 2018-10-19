package com.siyka.omron.fins.codec;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsPdu;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public interface FinsFrameDecoder<T extends FinsPdu> {

	public FinsFrame<T> decode(final ByteBuf buffer) throws DecoderException;
	
}
