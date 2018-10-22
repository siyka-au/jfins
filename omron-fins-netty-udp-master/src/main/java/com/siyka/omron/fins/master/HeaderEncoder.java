package com.siyka.omron.fins.master;

import java.util.function.BiFunction;

import com.siyka.omron.fins.FinsHeader;

import io.netty.buffer.ByteBuf;

public class HeaderEncoder {
	
	static BiFunction<ByteBuf, FinsHeader, ByteBuf> encode = (buffer, header) -> {
		return buffer;
	};
	
}
