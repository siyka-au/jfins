package com.siyka.omron.fins;

public class SimpleResponse implements FinsResponse {

	private final FinsCommandCode commandCode;

	private final FinsEndCode endCode;

	public SimpleResponse(FinsCommandCode commandCode, FinsEndCode endCode) {
		super();
		this.commandCode = commandCode;
		this.endCode = endCode;
	}

	public FinsCommandCode getCommandCode() {
		return this.commandCode;
	}

	public FinsEndCode getEndCode() {
		return this.endCode;
	}

}
