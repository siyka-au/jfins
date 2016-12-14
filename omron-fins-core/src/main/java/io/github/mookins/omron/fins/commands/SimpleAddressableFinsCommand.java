package io.github.mookins.omron.fins.commands;

import io.github.mookins.omron.fins.FinsCommandCode;
import io.github.mookins.omron.fins.FinsIoAddress;

public abstract class SimpleAddressableFinsCommand extends SimpleFinsCommand {

	private final FinsIoAddress ioAddress;

	public SimpleAddressableFinsCommand(final FinsCommandCode commandCode, final FinsIoAddress ioAddress) {
		super(commandCode);
		this.ioAddress = ioAddress;
	}

	public FinsIoAddress getIoAddress() {
		return this.ioAddress;
	}

}
