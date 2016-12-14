package io.github.mookins.omron.fins.commands;

import io.github.mookins.omron.fins.FinsCommandCode;

public interface FinsCommand {

	public FinsCommandCode getCommandCode();
	
	public byte[] getBytes();
	
}
