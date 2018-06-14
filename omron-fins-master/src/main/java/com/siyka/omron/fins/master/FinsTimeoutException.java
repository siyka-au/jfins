package com.siyka.omron.fins.master;

import java.time.Duration;

public class FinsTimeoutException extends RuntimeException {

	private static final long serialVersionUID = -5619209442732224706L;

	private final long durationMillis;

	public FinsTimeoutException(Duration duration) {
		this(duration.toMillis());
	}

	public FinsTimeoutException(long durationMillis) {
		this.durationMillis = durationMillis;
	}

	@Override
	public String getMessage() {
		return String.format("Request timed out after %sms milliseconds.", durationMillis);
	}

}
