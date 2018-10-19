package com.siyka.omron.fins.responses;

import java.util.Objects;

import com.siyka.omron.fins.BasePdu;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;

public abstract class AbstractResponse<T extends AbstractResponse<T>> extends BasePdu<T> implements FinsResponse<T> {

	private final FinsEndCode endCode;

	public AbstractResponse(final FinsCommandCode commandCode, final FinsEndCode endCode) {
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
