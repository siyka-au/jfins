package com.siyka.omron.fins;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;

public enum IoMemoryArea {
	
	CIO_BIT(0x30, 1),
	WR_BIT(0x31, 1),
	HR_BIT(0x32, 1),
	AR_BIT(0x33, 1),
	CIO_WORD(0xb0, 2),
	WR_WORD(0xb1, 2),
	HR_WORD(0xb2, 2),
	AR_WORD(0xb3, 2),
	TIMER_COUNTER_COMPLETION_FLAG(0x09, 1),
	TIMER_COUNTER_PV(0x89, 2),
	DM_BIT(0x02, 1),
	DM_WORD(0x82, 2),
	TASK_BIT(0x06, 1),
	TASK_STATUS(0x46, 1),
	INDEX_REGISTER_PV(0xdc, 4),
	DATA_REGISTER_PV(0xbc, 2),
	CLOCK_PULSES_CONDITION_FLAGS_BIT(0x07, 1);

	private final byte memoryAreaValue;
	private final int dataByteSize;

	private final static Map<Byte, IoMemoryArea> map = stream(IoMemoryArea.values()).collect(
			toMap(memoryAreaValue -> memoryAreaValue.memoryAreaValue, memoryAreaValue -> memoryAreaValue));

	private IoMemoryArea(final int memoryAreaValue, final int dataByteSize) {
		this.memoryAreaValue = (byte) memoryAreaValue;
		this.dataByteSize = dataByteSize;
	}

	public static Optional<IoMemoryArea> valueOf(byte memoryAreaValue) {
		return Optional.ofNullable(map.get(memoryAreaValue));
	}

	public byte getValue() {
		return this.memoryAreaValue;
	}
	
	public int getDataByteSize() {
		return this.dataByteSize;
	}
	
}
