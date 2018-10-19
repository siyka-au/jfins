package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.IoAddress;

public class MemoryAreaWriteWordCommand extends MemoryAreaWriteCommand<Short> {

	public MemoryAreaWriteWordCommand(final IoAddress ioAddress, final List<Short> dataItems) {
		super(ioAddress, dataItems);
	}

	public String toString() {
		return String.format("%s itemType[WORD]", super.toString());
	}
	
}
