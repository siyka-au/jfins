package com.siyka.omron.fins.commands;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.responses.MemoryAreaReadResponse;

public final class MemoryAreaReadCommand<T> extends FinsAddressableCommand implements FinsCommand<MemoryAreaReadResponse<T>> {

	private final short itemCount;

	public MemoryAreaReadCommand(final FinsIoAddress address, final int itemCount) {
		super(FinsCommandCode.MEMORY_AREA_READ, address);
		this.itemCount = (short) itemCount;
	}

	public short getItemCount() {
		return itemCount;
	}

}
