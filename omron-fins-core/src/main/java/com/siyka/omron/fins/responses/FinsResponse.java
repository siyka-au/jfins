package com.siyka.omron.fins.responses;

import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.FinsPdu;

public interface FinsResponse<T extends FinsResponse<T>> extends FinsPdu<T> {
	
	public FinsEndCode getEndCode();
	
}
