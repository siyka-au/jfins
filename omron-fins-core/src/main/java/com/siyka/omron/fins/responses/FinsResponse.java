package com.siyka.omron.fins.responses;

import com.siyka.omron.fins.ResponseCode;
import com.siyka.omron.fins.FinsPdu;

public interface FinsResponse extends FinsPdu {

	public ResponseCode getResponseCode();
	
}
