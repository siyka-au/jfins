package com.siyka.omron.fins;

public class UnsupportedPdu implements FinsPdu	 {

	private final CommandCode commandCode;

	public UnsupportedPdu(CommandCode commandCode) {
		this.commandCode = commandCode;
	}

	@Override
	public CommandCode getCommandCode() {
		return commandCode;
	}

}
