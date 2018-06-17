package com.siyka.omron.fins;

public class BitWithForce extends Bit implements Forceable<Boolean> {

	private static final long serialVersionUID = -8272612474074900238L;
	
	private final boolean forced;
	
	public BitWithForce(final boolean value, final boolean forced) {
		super(value);
		this.forced = forced;
	}

	@Override
	public Boolean getForceMask() {
		return this.forced;
	}
	
}
