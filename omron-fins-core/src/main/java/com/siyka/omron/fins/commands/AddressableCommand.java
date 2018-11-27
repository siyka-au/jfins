package com.siyka.omron.fins.commands;

import com.siyka.omron.fins.FinsIoAddress;

public abstract class AddressableCommand implements FinsCommand {

	private final FinsIoAddress address;
	
	public AddressableCommand(final FinsIoAddress address) {
		 this.address = address;
	}
	
	public FinsIoAddress getAddress() {
		return this.address;
	}
	
}
