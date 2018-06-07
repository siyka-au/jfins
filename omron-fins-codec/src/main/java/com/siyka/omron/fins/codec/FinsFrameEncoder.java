package com.siyka.omron.fins.codec;

import com.siyka.omron.fins.FinsFrame;

import io.netty.buffer.ByteBuf;

public interface FinsFrameEncoder {

	public ByteBuf encode(final FinsFrame finsFrame);
	
}
