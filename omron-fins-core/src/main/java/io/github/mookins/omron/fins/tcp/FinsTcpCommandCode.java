package io.github.mookins.omron.fins.tcp;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;

public enum FinsTcpCommandCode {
	
	FINS_CLIENT_NODE_ADDRESS_DATA_SEND(0x0000),
	FINS_SERVER_NODE_ADDRESS_DATA_SEND(0x0001),
	FINS_FRAME_SEND(0x0002), 
	FINS_FRAME_SEND_ERROR_NOTIFCATION(0x0003),
	CONNECTION_CONFIRMATION(0x0006);

	int commandCodeValue;

	FinsTcpCommandCode(final int commandCodeValue) {
		this.commandCodeValue = commandCodeValue;
	}

	private final static Map<Integer, FinsTcpCommandCode> map = stream(FinsTcpCommandCode.values())
			.collect(toMap(commandCode -> commandCode.commandCodeValue, commandCode -> commandCode));

	public static Optional<FinsTcpCommandCode> valueOf(int commandCodeValue) {
		return Optional.ofNullable(map.get(commandCodeValue));
	}

	public int getValue() {
		return this.commandCodeValue;
	}
	
}
