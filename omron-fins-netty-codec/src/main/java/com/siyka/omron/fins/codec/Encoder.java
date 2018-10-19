package com.siyka.omron.fins.codec;

import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface Encoder<T> {

	public ByteBuf encode(final T object);
	
}
