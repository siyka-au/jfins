package com.siyka.omron.fins.wip;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.commands.FinsAddressableCommand;

public final class MemoryAreaReadCommand extends FinsAddressableCommand {

	private final short itemCount;

	public MemoryAreaReadCommand(final FinsIoAddress address, final short itemCount) {
		super(FinsCommandCode.MEMORY_AREA_READ, address);
		this.itemCount = itemCount;
	}

	public MemoryAreaReadCommand(final FinsHeader header, final FinsIoAddress address, final int itemCount) {
		this(address, (short) itemCount);
	}

	public short getItemCount() {
		return itemCount;
	}

}
