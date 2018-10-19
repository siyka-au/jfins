package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.IoAddress;

public class MemoryAreaWriteBitCommand extends MemoryAreaWriteCommand<Bit> {

	public MemoryAreaWriteBitCommand(final IoAddress ioAddress, final List<Bit> dataItems) {
		super(ioAddress, dataItems);
	}

	public String toString() {
		return String.format("%s itemType[BIT]", super.toString());
	}

}
