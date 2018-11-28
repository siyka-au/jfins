package com.siyka.omron.fins.commands;

import com.siyka.omron.fins.ByteAlign;
import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.FinsIoAddress;

public class MemoryAreaWriteCommand extends AddressableCommand {

	private final int itemCount;
	private final byte[] data;

	public MemoryAreaWriteCommand(FinsIoAddress address, byte[] data) {
		super(address);
		this.data = ByteAlign.align(data);
		if (this.data.length % address.getMemoryArea().getDataByteSize() != 0) {
			throw new IllegalArgumentException(String.format("Address area %s required integer multiple bytes of size %d", address.getMemoryArea(), address.getMemoryArea().getDataByteSize()));
		}
		this.itemCount = this.data.length / address.getMemoryArea().getDataByteSize();
	}

	@Override
	public CommandCode getCommandCode() {
		return CommandCode.MEMORY_AREA_WRITE;
	}
	
	public int getItemCount() {
		return this.itemCount;
	}

	public byte[] getData() {
		return this.data;
	}

}
