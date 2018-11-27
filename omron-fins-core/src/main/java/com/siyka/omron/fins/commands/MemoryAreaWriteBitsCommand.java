package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsIoAddress;

public class MemoryAreaWriteBitsCommand extends MemoryAreaWriteCommand<Bit> {

	public MemoryAreaWriteBitsCommand(final FinsIoAddress address, final List<Bit> items) {
		super(address, items);
	}

}
