package io.bessel.fins;

import io.bessel.fins.commands.FinsMemoryAreaWriteCommand;
import io.bessel.fins.commands.FinsMemoryAreaWriteResponse;

@FunctionalInterface
public interface MemoryAreaWriteCommandHandler {

	public FinsMemoryAreaWriteResponse handle(FinsMemoryAreaWriteCommand command);
	
}
