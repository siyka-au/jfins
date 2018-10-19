package com.siyka.omron.fins.codec;

import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface Decoder<T> {

	public T decode(final ByteBuf buffer);
	
}
