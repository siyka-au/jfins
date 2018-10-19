package com.siyka.omron.fins.responses;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;

public final class SimpleResponse extends AbstractResponse<SimpleResponse> {

	public SimpleResponse(final FinsCommandCode commandCode, final FinsEndCode endCode) {
		super(commandCode, endCode);
	}

}
