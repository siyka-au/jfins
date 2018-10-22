package com.siyka.omron.fins;

import java.util.List;

public class MemoryAreaWriteCommand<T extends DataItem<?>> extends AddressableCommand {

	private final List<T> items;

	public MemoryAreaWriteCommand(final FinsIoAddress address, final List<T> items) {
		super(address);
		this.items = items;
	}

	@Override
	public FinsCommandCode getCommandCode() {
		return FinsCommandCode.MEMORY_AREA_WRITE;
	}

	public List<T> getItems() {
		return this.items;
	}

}
