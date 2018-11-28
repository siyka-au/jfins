package com.siyka.omron.fins;

import com.siyka.omron.fins.FinsPdu;

import io.netty.buffer.ByteBuf;

public interface FinsPduEncoder<T extends FinsPdu> {

    public ByteBuf encode(final T pdu, ByteBuf buffer);
	
}
