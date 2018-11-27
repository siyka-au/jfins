package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.Word;

public class MemoryAreaWriteWordsCommand extends MemoryAreaWriteCommand<Word> {

	public MemoryAreaWriteWordsCommand(final FinsIoAddress address, final List<Word> items) {
		super(address, items);
	}

}
