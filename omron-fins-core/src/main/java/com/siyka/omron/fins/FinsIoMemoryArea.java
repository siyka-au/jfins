package com.siyka.omron.fins;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;

public enum FinsIoMemoryArea {
	
	CIO_BIT(0x30, 1, FinsDataType.BIT),
	WR_BIT(0x31, 1, FinsDataType.BIT),
	HR_BIT(0x32, 1, FinsDataType.BIT),
	AR_BIT(0x33, 1, FinsDataType.BIT),
	
	CIO_BIT_WITH_FORCE(0x70, 1, FinsDataType.BIT_WITH_FORCE),
	WR_BIT_WITH_FORCE(0x71, 1, FinsDataType.BIT_WITH_FORCE),
	HR_BIT_WITH_FORCE(0x72, 1, FinsDataType.BIT_WITH_FORCE),
	
	CIO_WORD(0xb0, 2, FinsDataType.WORD),
	WR_WORD(0xb1, 2, FinsDataType.WORD),
	HR_WORD(0xb2, 2, FinsDataType.WORD),
	AR_WORD(0xb3, 2, FinsDataType.WORD),
	
	CIO_WORD_WITH_FORCE(0xf0, 2, FinsDataType.WORD_WITH_FORCE),
	WR_WORD_WITH_FORCE(0xf1, 2, FinsDataType.WORD_WITH_FORCE),
	HR_WORD_WITH_FORCE(0xf2, 2, FinsDataType.WORD_WITH_FORCE),
	
	TIMER_COUNTER_COMPLETION_FLAG(0x09, 1, FinsDataType.COMPLETION_FLAG),
	
	TIMER_COUNTER_COMPLETION_FLAG_WITH_FORCE(0x49, 1, FinsDataType.COMPLETION_FLAG_WITH_FORCE),
	
	TIMER_COUNTER_PV(0x89, 2, FinsDataType.WORD),
	
	DM_BIT(0x02, 1, FinsDataType.BIT),
	DM_WORD(0x82, 2, FinsDataType.WORD),
	
	TASK_BIT(0x06, 1, FinsDataType.BIT),
	TASK_STATUS(0x46, 1, FinsDataType.STATUS),
	
	INDEX_REGISTER_PV(0xdc, 4, FinsDataType.PV),
	DATA_REGISTER_PV(0xbc, 2, FinsDataType.PV),
	
	CLOCK_PULSES_CONDITION_FLAGS_BIT(0x07, 1, FinsDataType.BIT);

	private final byte memoryAreaValue;
	private final int dataByteSize;
	private final FinsDataType dataType;

	private final static Map<Byte, FinsIoMemoryArea> map = stream(FinsIoMemoryArea.values()).collect(
			toMap(memoryAreaValue -> memoryAreaValue.memoryAreaValue, memoryAreaValue -> memoryAreaValue));

	private FinsIoMemoryArea(final int memoryAreaValue, final int dataByteSize, final FinsDataType dataType) {
		this.memoryAreaValue = (byte) memoryAreaValue;
		this.dataByteSize = dataByteSize;
		this.dataType = dataType;
	}

	public static Optional<FinsIoMemoryArea> valueOf(byte memoryAreaValue) {
		return Optional.ofNullable(map.get(memoryAreaValue));
	}

	public byte getValue() {
		return this.memoryAreaValue;
	}
	
	public FinsDataType getDataType() {
		return this.dataType;
	}
	
	public int getDataByteSize() {
		return this.dataByteSize;
	}
	
}
