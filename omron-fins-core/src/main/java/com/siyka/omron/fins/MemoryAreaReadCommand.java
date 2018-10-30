package com.siyka.omron.fins;

public class MemoryAreaReadCommand extends AddressableCommand {

	private final int itemCount;

	public MemoryAreaReadCommand(final FinsIoAddress address, final int itemCount) {
		super(address);
		this.itemCount = itemCount;
	}

	@Override
	public FinsCommandCode getCommandCode() {
		return FinsCommandCode.MEMORY_AREA_READ;
	}

	public int getItemCount() {
		return itemCount;
	}

}
