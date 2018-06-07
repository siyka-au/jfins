package com.siyka.omron.fins.commands;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsIoAddress;

public abstract class MemoryAreaWriteCommand<T> extends FinsAddressableCommand {

	private final List<T> dataItems;
	
	public MemoryAreaWriteCommand(final FinsCommandCode commandCode, final FinsIoAddress ioAddress, final List<T> dataItems) {
		super(commandCode, ioAddress);
		Objects.requireNonNull(dataItems);
		if (dataItems.getClass().getSimpleName().equals("UnmodifiableCollection")) {
			this.dataItems = dataItems;
		} else {
			this.dataItems = Collections.unmodifiableList(dataItems);
		}
	}

	public List<T> getDataItems() {
		return dataItems;
	}
	
	public String toString() {
		return String.format("%s addr[%d.%d] itemCount[%d]",
				super.toString(),
				this.getIoAddress().getAddress(),
				this.getIoAddress().getBitOffset(),
				this.dataItems.size());
	}
	
}
