package com.siyka.omron.fins.responses;

import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.FinsPdu;

public interface FinsResponse extends FinsPdu {
	
	public FinsEndCode getEndCode();
	
}
