package io.bessel.fins;

public class FinsFrameException extends Exception {

	private static final long serialVersionUID = -8075282543790954850L;

	public FinsFrameException() {
		super();
	}

	public FinsFrameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FinsFrameException(String message, Throwable cause) {
		super(message, cause);
	}

	public FinsFrameException(String message) {
		super(message);
	}

	public FinsFrameException(Throwable cause) {
		super(cause);
	}
	
}
