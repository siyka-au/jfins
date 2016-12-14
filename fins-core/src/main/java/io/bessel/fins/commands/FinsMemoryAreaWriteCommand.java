package io.bessel.fins.commands;

import io.bessel.fins.FinsCommandCode;
import io.bessel.fins.FinsIoAddress;

public abstract class FinsMemoryAreaWriteCommand extends SimpleAddressableFinsCommand {

	public FinsMemoryAreaWriteCommand(FinsCommandCode commandCode, FinsIoAddress ioAddress) {
		super(commandCode, ioAddress);
	}
	
}
