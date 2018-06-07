package com.siyka.omron.fins.slave;

public class FinsSlaveException extends Exception {

	private static final long serialVersionUID = 2034734624679243932L;

	public FinsSlaveException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FinsSlaveException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FinsSlaveException(final String message) {
		super(message);
	}

	public FinsSlaveException(final Throwable cause) {
		super(cause);
	}
	
}
