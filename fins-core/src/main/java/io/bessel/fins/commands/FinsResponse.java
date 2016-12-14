package io.bessel.fins.commands;

import io.bessel.fins.FinsEndCode;
import io.bessel.fins.FinsCommandCode;

public interface FinsResponse {

	public FinsCommandCode getCommandCode();
	
	public FinsEndCode getEndCode();
	
	public byte[] getBytes();
	
}
