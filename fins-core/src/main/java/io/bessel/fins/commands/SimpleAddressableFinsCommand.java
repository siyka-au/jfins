package io.bessel.fins.commands;

import io.bessel.fins.FinsCommandCode;
import io.bessel.fins.FinsIoAddress;

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
