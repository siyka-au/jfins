package com.siyka.omron.fins;

import java.util.List;

public class MemoryAreaWriteWordsCommand extends MemoryAreaWriteCommand<Word> {

	public MemoryAreaWriteWordsCommand(final FinsIoAddress address, final List<Word> items) {
		super(address, items);
	}

}
