package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsIoAddress;

public class MemoryAreaWriteWordCommand extends MemoryAreaWriteCommand<Short> {

	public MemoryAreaWriteWordCommand(final FinsCommandCode commandCode, final FinsIoAddress ioAddress, final List<Short> items) {
		super(commandCode, ioAddress, items);
	}
	
}
