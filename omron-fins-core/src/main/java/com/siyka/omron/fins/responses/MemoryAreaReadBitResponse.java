package com.siyka.omron.fins.responses;

import java.util.List;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsEndCode;

public class MemoryAreaReadBitResponse extends MemoryAreaReadResponse<MemoryAreaReadBitResponse, Bit> {

	public MemoryAreaReadBitResponse(final FinsEndCode errorCode, final List<Bit> items) {
		super(errorCode, items);
	}

}
