package com.siyka.omron.fins.codec;

import java.nio.ByteBuffer;

import com.siyka.omron.fins.FinsFrame;

public interface FinsFrameEncoder {

	public ByteBuffer encode(final FinsFrame finsFrame);
	
}
