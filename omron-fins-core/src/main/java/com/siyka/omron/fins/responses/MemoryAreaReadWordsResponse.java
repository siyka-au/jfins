package com.siyka.omron.fins.responses;

import java.util.List;

import com.siyka.omron.fins.EndCode;
import com.siyka.omron.fins.Word;

public class MemoryAreaReadWordsResponse extends MemoryAreaReadResponse<Word> {

	public MemoryAreaReadWordsResponse(final EndCode endCode, final List<Word> items) {
		super(endCode, items);
	}

}
