package com.siyka.omron.fins.codec;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsPdu;

import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface FinsFrameEncoder<T extends FinsPdu> {

	public ByteBuf encode(final FinsFrame finsFrame);
	
}
