package com.siyka.omron.fins;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;

public enum CommandCode {
	
	MEMORY_AREA_READ(0x0101),
	MEMORY_AREA_WRITE(0x0102),
	MEMORY_AREA_FILL(0x0103),
	MULTIPLE_MEMORY_AREA_READ(0x0104),
	MEMORY_AREA_TRANSFER(0x0105),
	PARAMETER_AREA_READ(0x0201),
	PARAMETER_AREA_WRITE(0x0202),
	PARAMETER_AREA_CLEAR(0x0203),
	PROGRAM_AREA_READ(0x0301),
	PROGRAM_AREA_WRITE(0x0302),
	PROGRAM_AREA_CLEAR(0x0303),
	RUN(0x0401),
	STOP(0x0402),
	CPU_UNIT_DATA_READ(0x0501),
	CONNECTION_DATA_READ(0x0502),
	CPU_UNIT_STATUS_READ(0x0601),
	CYCLE_TIME_READ(0x0620),
	
	UNKNOWN(0xffff);

	private final short commandCodeValue;

	private final static Map<Short, CommandCode> map = stream(CommandCode.values()).collect(
			toMap(commandCodeValue -> commandCodeValue.commandCodeValue, commandCodeValue -> commandCodeValue));

	private CommandCode(final short commandCodeValue) {
		this.commandCodeValue = commandCodeValue;
	}

	private CommandCode(final int commandCodeValue) {
		this((short) commandCodeValue);
	}

	public static Optional<CommandCode> valueOf(final short commandCodeValue) {
		return Optional.ofNullable(map.get(commandCodeValue));
	}

	public static Optional<CommandCode> valueOf(final int commandCodeValue) {
		return CommandCode.valueOf((short) commandCodeValue);
	}

	public short getValue() {
		return this.commandCodeValue;
	}
	
}
