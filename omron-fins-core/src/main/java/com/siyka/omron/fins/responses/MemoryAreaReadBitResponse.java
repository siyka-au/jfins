package com.siyka.omron.fins.responses;

import java.util.List;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;

public final class MemoryAreaReadBitResponse extends MemoryAreaReadResponse<Bit> {

	public MemoryAreaReadBitResponse(final FinsCommandCode commandCode, final FinsEndCode endCode, final List<Bit> items) {
		super(commandCode, endCode, items);
	}
	
}
