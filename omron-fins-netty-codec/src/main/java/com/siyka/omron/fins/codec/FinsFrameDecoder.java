package com.siyka.omron.fins.codec;

import com.siyka.omron.fins.FinsPdu;
import com.siyka.omron.fins.FinsFrame;

import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface FinsFrameDecoder<T extends FinsPdu> {

	public FinsFrame decode(final ByteBuf buffer);
	
}
