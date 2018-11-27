package com.siyka.omron.fins.master;

import com.siyka.omron.fins.FinsPdu;

import io.netty.buffer.ByteBuf;

public interface FinsPduEncoder {

    public ByteBuf encode(final FinsPdu modbusPdu, ByteBuf buffer);
	
}
