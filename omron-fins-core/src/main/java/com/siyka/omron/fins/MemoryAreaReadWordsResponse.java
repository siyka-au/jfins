package com.siyka.omron.fins;

import java.util.List;

public class MemoryAreaReadWordsResponse extends MemoryAreaReadResponse<Word> {

	public MemoryAreaReadWordsResponse(final FinsEndCode endCode, final List<Word> items) {
		super(endCode, items);
	}

}
