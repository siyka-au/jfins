package io.bessel.fins.commands;

import io.bessel.fins.FinsCommandCode;
import io.bessel.fins.FinsEndCode;

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
