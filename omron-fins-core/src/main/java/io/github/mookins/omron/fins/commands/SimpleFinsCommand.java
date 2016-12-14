package io.github.mookins.omron.fins.commands;

import io.github.mookins.omron.fins.FinsCommandCode;

public abstract class SimpleFinsCommand implements FinsCommand {

	private final FinsCommandCode commandCode;

	protected SimpleFinsCommand(FinsCommandCode commandCode) {
		this.commandCode = commandCode;
	}

	@Override
	public FinsCommandCode getCommandCode() {
		return this.commandCode;
	}

}
