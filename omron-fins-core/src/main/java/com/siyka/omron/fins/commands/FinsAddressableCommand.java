package com.siyka.omron.fins.commands;

import java.util.Objects;

import com.siyka.omron.fins.FinsBasePdu;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsIoAddress;

public abstract class FinsAddressableCommand extends FinsBasePdu implements FinsCommand {

	private final FinsIoAddress ioAddress;

	public FinsAddressableCommand(final FinsCommandCode commandCode,  final FinsIoAddress ioAddress) {
		super(commandCode);
		Objects.requireNonNull(ioAddress);
		this.ioAddress = ioAddress;
	}

	public FinsIoAddress getIoAddress() {
		return this.ioAddress;
	}
	
}
