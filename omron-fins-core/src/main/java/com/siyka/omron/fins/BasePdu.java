package com.siyka.omron.fins;

public abstract class BasePdu<T extends FinsPdu<T>> implements FinsPdu<T> {

	private final FinsCommandCode commandCode;
	
	public BasePdu(FinsCommandCode commandCode) {
		super();
		this.commandCode = commandCode;
	}

	@Override
	public FinsCommandCode getCommandCode() {
		return this.commandCode;
	}

}
