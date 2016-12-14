package io.bessel.fins.commands;

import io.bessel.fins.FinsCommandCode;

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
