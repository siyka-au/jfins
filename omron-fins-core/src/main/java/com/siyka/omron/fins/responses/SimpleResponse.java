package com.siyka.omron.fins.responses;

import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.ResponseCode;

public class SimpleResponse implements FinsResponse {

	private final CommandCode commandCode;
	private final ResponseCode responseCode;

	public SimpleResponse(CommandCode commandCode, ResponseCode responseCode) {
		super();
		this.commandCode = commandCode;
		this.responseCode = responseCode;
	}

	public CommandCode getCommandCode() {
		return this.commandCode;
	}

	public ResponseCode getResponseCode() {
		return this.responseCode;
	}

}
