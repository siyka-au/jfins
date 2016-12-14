package io.github.mookins.omron.fins.commands;

import io.github.mookins.omron.fins.FinsCommandCode;
import io.github.mookins.omron.fins.FinsIoAddress;

public abstract class FinsMemoryAreaWriteCommand extends SimpleAddressableFinsCommand {

	public FinsMemoryAreaWriteCommand(FinsCommandCode commandCode, FinsIoAddress ioAddress) {
		super(commandCode, ioAddress);
	}
	
}
