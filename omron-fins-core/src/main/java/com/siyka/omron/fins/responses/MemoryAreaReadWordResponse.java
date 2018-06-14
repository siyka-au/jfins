package com.siyka.omron.fins.responses;

import java.util.Arrays;
import java.util.List;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;

public final class MemoryAreaReadWordResponse extends MemoryAreaReadResponse<Short> {

	public MemoryAreaReadWordResponse(final FinsCommandCode commandCode, final FinsEndCode endCode, final List<Short> items) {
		super(commandCode, endCode, items);
	}

	public String toString() {
		return String.format("itemCount[%d] items%s",
				this.getItems().size(),
				Arrays.toString(this.getItems().toArray()));
	}
	
}
