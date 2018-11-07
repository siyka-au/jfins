package com.siyka.omron.fins;

import java.nio.ByteBuffer;

public interface FinsPdu {
	
	public FinsCommandCode getCommandCode();

	public ByteBuffer encode();
	
}
