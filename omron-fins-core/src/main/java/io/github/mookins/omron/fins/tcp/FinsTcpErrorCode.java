package io.github.mookins.omron.fins.tcp;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;

public enum FinsTcpErrorCode {
	
	NORMAL(0x0000),
	HEADER_NOT_FINS(0x0001),
	DATA_LENGTH_TOO_LONG(0x0002),
	COMMAND_NOT_SUPPORTED(0x0003),
	ALL_CONNECTIONS_ARE_IN_USE(0x0020),
	SPECIFIED_NODE_IS_ALREADY_CONNECTED(0x0021),
	ATTEMPTED_PROTECTED_CONNECTION_FROM_UNSPECIFIED_IP_ADDRESS(0x0022),
	FINS_NODE_OUT_OF_RANGE(0x0023),
	FINS_NODE_SERVER_CLIENT_SAME(0x0024),
	FINS_NODE_ADDRESSES_AVAILABLE_EXHAUSTED(0x0025);

	int errorCodeValue;

	FinsTcpErrorCode(final int errorCodeValue) {
		this.errorCodeValue = errorCodeValue;
	}

	private final static Map<Integer, FinsTcpErrorCode> map = stream(FinsTcpErrorCode.values())
			.collect(toMap(errorCode -> errorCode.errorCodeValue, errorCode -> errorCode));

	public static Optional<FinsTcpErrorCode> valueOf(int errorCodeValue) {
		return Optional.ofNullable(map.get(errorCodeValue));
	}

	public int getValue() {
		return this.errorCodeValue;
	}
}
