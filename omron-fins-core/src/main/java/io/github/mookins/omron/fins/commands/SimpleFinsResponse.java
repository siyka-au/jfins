package io.github.mookins.omron.fins.commands;

import io.github.mookins.omron.fins.FinsCommandCode;
import io.github.mookins.omron.fins.FinsEndCode;

public abstract class SimpleFinsResponse implements FinsResponse {

	private final FinsCommandCode commandCode;
	private final FinsEndCode endCode;

	protected SimpleFinsResponse(final FinsCommandCode commandCode, final FinsEndCode endCode) {
		this.commandCode = commandCode;
		this.endCode = endCode;
	}

	@Override
	public FinsCommandCode getCommandCode() {
		return this.commandCode;
	}

	@Override
	public FinsEndCode getEndCode() {
		return this.endCode;
	}

}
