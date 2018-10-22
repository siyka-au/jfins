package com.siyka.omron.fins;

public class MemoryAreaReadCommand extends AddressableCommand {

	
	private final short itemCount;

	public MemoryAreaReadCommand(final FinsIoAddress address, final short itemCount) {
		super(address);
		this.itemCount = itemCount;
	}

	@Override
	public FinsCommandCode getCommandCode() {
		return FinsCommandCode.MEMORY_AREA_READ;
	}

	public short getItemCount() {
		return itemCount;
	}

}
