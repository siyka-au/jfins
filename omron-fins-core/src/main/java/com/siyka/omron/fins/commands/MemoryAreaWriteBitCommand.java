package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsIoAddress;

public class MemoryAreaWriteBitCommand extends MemoryAreaWriteCommand<Bit> {

	public MemoryAreaWriteBitCommand(final FinsIoAddress ioAddress, final List<Bit> items) {
		super(ioAddress, items);
	}
	
}
