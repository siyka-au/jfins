package com.siyka.omron.fins.responses;

import com.siyka.omron.fins.EndCode;
import com.siyka.omron.fins.FinsPdu;

public interface FinsResponse extends FinsPdu {

	public EndCode getEndCode();
	
}
