package com.siyka.omron.fins.responses;

import java.util.List;

import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.Word;

public class MemoryAreaReadWordResponse extends MemoryAreaReadResponse<MemoryAreaReadWordResponse, Word> {

	public MemoryAreaReadWordResponse(final FinsEndCode errorCode, final List<Word> items) {
		super(errorCode, items);
	}

}
