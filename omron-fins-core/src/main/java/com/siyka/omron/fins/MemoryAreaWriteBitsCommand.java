package com.siyka.omron.fins;

import java.util.List;

public class MemoryAreaWriteBitsCommand extends MemoryAreaWriteCommand<Bit> {

	public MemoryAreaWriteBitsCommand(final FinsIoAddress address, final List<Bit> items) {
		super(address, items);
	}

}
