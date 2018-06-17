package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.Word;

public class MemoryAreaWriteWordCommand extends MemoryAreaWriteCommand<Word> {

	public MemoryAreaWriteWordCommand(final FinsIoAddress ioAddress, final List<Word> items) {
		super(ioAddress, items);
	}
	
}
