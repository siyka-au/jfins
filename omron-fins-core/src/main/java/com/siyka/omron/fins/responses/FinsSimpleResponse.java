package com.siyka.omron.fins.responses;

import java.util.Objects;

import com.siyka.omron.fins.FinsBasePdu;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;

public class FinsSimpleResponse extends FinsBasePdu implements FinsResponse {

	private final FinsEndCode endCode;

	public FinsSimpleResponse(final FinsCommandCode commandCode, final FinsEndCode endCode) {
		super(commandCode);
		Objects.requireNonNull(endCode);
		this.endCode = endCode;
	}

	@Override
	public FinsEndCode getEndCode() {
		return this.endCode;
	}
	
	public String toString() {
		return String.format("%s endCode[%s]",
				super.toString(),
				this.getEndCode());
	}
	
}
