package com.siyka.omron.fins.commands;

import com.siyka.omron.fins.FinsPdu;
import com.siyka.omron.fins.responses.FinsResponse;

public interface FinsCommand<Response extends FinsResponse> extends FinsPdu {
	
}
