package io.github.mookins.omron.fins.tcp;

public class FinsTcpFrameException extends Exception {

	private static final long serialVersionUID = 2938015920094636644L;

	public FinsTcpFrameException() {
		super();
	}

	public FinsTcpFrameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FinsTcpFrameException(String message, Throwable cause) {
		super(message, cause);
	}

	public FinsTcpFrameException(String message) {
		super(message);
	}

	public FinsTcpFrameException(Throwable cause) {
		super(cause);
	}
}
