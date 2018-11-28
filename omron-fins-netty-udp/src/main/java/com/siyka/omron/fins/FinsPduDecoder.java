package com.siyka.omron.fins;

import com.siyka.omron.fins.FinsPdu;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public interface FinsPduDecoder<T extends FinsPdu> {

	public T decode(ByteBuf buffer) throws DecoderException;
	
}
