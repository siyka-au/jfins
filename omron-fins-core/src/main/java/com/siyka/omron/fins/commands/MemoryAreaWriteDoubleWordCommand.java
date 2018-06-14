package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsIoAddress;

public class MemoryAreaWriteDoubleWordCommand extends MemoryAreaWriteCommand<Integer> {

	public MemoryAreaWriteDoubleWordCommand(final FinsCommandCode commandCode, final FinsIoAddress ioAddress, final List<Integer> items) {
		super(commandCode, ioAddress, items);
	}

}
