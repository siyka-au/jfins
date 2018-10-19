package com.siyka.omron.fins.commands;

import java.util.List;

import com.siyka.omron.fins.IoAddress;

public class MemoryAreaWriteDoubleWordCommand extends MemoryAreaWriteCommand<Integer> {

	public MemoryAreaWriteDoubleWordCommand(final IoAddress ioAddress, final List<Integer> dataItems) {
		super(ioAddress, dataItems);
	}

	public String toString() {
		return String.format("%s itemType[DWORD]", super.toString());
	}
	
}
