package com.siyka.omron.fins;

public abstract class FinsBasePdu implements FinsPdu {

	private final FinsCommandCode commandCode;
	
	public FinsBasePdu(FinsCommandCode commandCode) {
		super();
		this.commandCode = commandCode;
	}

	@Override
	public FinsCommandCode getCommandCode() {
		return this.commandCode;
	}

}
