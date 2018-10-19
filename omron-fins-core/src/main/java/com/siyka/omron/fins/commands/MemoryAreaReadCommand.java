package com.siyka.omron.fins.commands;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.IoAddress;

public final class MemoryAreaReadCommand extends AddressableCommand<MemoryAreaReadCommand> {

	private final short itemCount;

	public MemoryAreaReadCommand(final IoAddress address, final short itemCount) {
		super(FinsCommandCode.MEMORY_AREA_READ, address);
		this.itemCount = itemCount;
	}

	public MemoryAreaReadCommand(final FinsHeader header, final IoAddress address, final int itemCount) {
		this(address, (short) itemCount);
	}

	public short getItemCount() {
		return itemCount;
	}

}
