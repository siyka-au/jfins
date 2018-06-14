package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsIoAddress;

public class MemoryAreaWriteBitCommand extends MemoryAreaWriteCommand<Bit> {

	public MemoryAreaWriteBitCommand(final FinsCommandCode commandCode, final FinsIoAddress ioAddress, final List<Bit> items) {
		super(commandCode, ioAddress, items);
	}
	
}
