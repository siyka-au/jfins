package com.siyka.omron.fins.responses;

import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.EndCode;

public class SimpleResponse implements FinsResponse {

	private final CommandCode commandCode;

	private final EndCode endCode;

	public SimpleResponse(CommandCode commandCode, EndCode endCode) {
		super();
		this.commandCode = commandCode;
		this.endCode = endCode;
	}

	public CommandCode getCommandCode() {
		return this.commandCode;
	}

	public EndCode getEndCode() {
		return this.endCode;
	}

}
