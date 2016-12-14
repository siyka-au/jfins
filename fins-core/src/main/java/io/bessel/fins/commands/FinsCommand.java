package io.bessel.fins.commands;

import io.bessel.fins.FinsCommandCode;

public interface FinsCommand {

	public FinsCommandCode getCommandCode();
	
	public byte[] getBytes();
	
}
