package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.DataItem;
import com.siyka.omron.fins.FinsIoAddress;

public class MemoryAreaWriteCommand<T extends DataItem<?>> extends AddressableCommand {

	private final List<T> items;

	public MemoryAreaWriteCommand(final FinsIoAddress address, final List<T> items) {
		super(address);
		this.items = items;
	}

	@Override
	public CommandCode getCommandCode() {
		return CommandCode.MEMORY_AREA_WRITE;
	}

	public List<T> getItems() {
		return this.items;
	}

}
