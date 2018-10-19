package com.siyka.omron.fins.commands;

import java.util.Objects;

import com.siyka.omron.fins.BasePdu;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.IoAddress;

public abstract class AddressableCommand<T extends FinsCommand<T>> extends BasePdu<T> implements FinsCommand<T> {

	private final IoAddress ioAddress;

	public AddressableCommand(final FinsCommandCode commandCode,  final IoAddress ioAddress) {
		super(commandCode);
		Objects.requireNonNull(ioAddress);
		this.ioAddress = ioAddress;
	}

	public IoAddress getIoAddress() {
		return this.ioAddress;
	}
	
}
