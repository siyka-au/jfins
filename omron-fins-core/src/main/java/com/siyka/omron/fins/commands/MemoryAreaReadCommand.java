package com.siyka.omron.fins.commands;

import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.FinsIoAddress;

public class MemoryAreaReadCommand extends AddressableCommand {

	private final int itemCount;

	public MemoryAreaReadCommand(final FinsIoAddress address, final int itemCount) {
		super(address);
		this.itemCount = itemCount;
	}

	@Override
	public CommandCode getCommandCode() {
		return CommandCode.MEMORY_AREA_READ;
	}

	public int getItemCount() {
		return itemCount;
	}

}
