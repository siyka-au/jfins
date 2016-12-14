package io.github.mookins.omron.fins;

import io.github.mookins.omron.fins.commands.FinsMemoryAreaWriteCommand;
import io.github.mookins.omron.fins.commands.FinsMemoryAreaWriteResponse;

@FunctionalInterface
public interface MemoryAreaWriteCommandHandler {

	public FinsMemoryAreaWriteResponse handle(FinsMemoryAreaWriteCommand command);
	
}
