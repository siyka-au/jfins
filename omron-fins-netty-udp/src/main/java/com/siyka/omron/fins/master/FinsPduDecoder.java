package com.siyka.omron.fins.master;

import com.siyka.omron.fins.FinsPdu;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public interface FinsPduDecoder {

	public FinsPdu decode(ByteBuf buffer) throws DecoderException;
	
}
