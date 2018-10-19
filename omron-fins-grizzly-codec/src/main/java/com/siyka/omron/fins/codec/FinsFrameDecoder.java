package com.siyka.omron.fins.codec;

import java.nio.ByteBuffer;

import com.siyka.omron.fins.FinsFrame;


public interface FinsFrameDecoder {

	public FinsFrame decode(final ByteBuffer buffer);
	
}
