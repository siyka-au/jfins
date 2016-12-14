package io.github.mookins.omron.fins.commands;

import io.github.mookins.omron.fins.FinsCommandCode;
import io.github.mookins.omron.fins.FinsEndCode;

public interface FinsResponse {

	public FinsCommandCode getCommandCode();
	
	public FinsEndCode getEndCode();
	
	public byte[] getBytes();
	
}
