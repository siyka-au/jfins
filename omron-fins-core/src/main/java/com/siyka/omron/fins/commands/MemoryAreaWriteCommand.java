package com.siyka.omron.fins.commands;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.responses.FinsSimpleResponse;

public abstract class MemoryAreaWriteCommand<T> extends FinsAddressableCommand implements FinsCommand<FinsSimpleResponse> {

	private final List<T> items;
	
	public MemoryAreaWriteCommand(final FinsCommandCode commandCode, final FinsIoAddress ioAddress, final List<T> items) {
		super(commandCode, ioAddress);
		Objects.requireNonNull(items);
		if (items.getClass().getSimpleName().equals("UnmodifiableCollection")) {
			this.items = items;
		} else {
			this.items = Collections.unmodifiableList(items);
		}
	}

	public List<T> getItems() {
		return items;
	}
	
	public String toString() {
		return String.format("%s addr[%d.%d] itemCount[%d] itemType[%s]",
				super.toString(),
				this.getIoAddress().getAddress(),
				this.getIoAddress().getBitOffset(),
				this.items.size(),
				this.getIoAddress().getMemoryArea().getDataType());
	}
	
}
