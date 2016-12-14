package io.bessel.fins;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;

public enum FinsEndCode {

	// Normal completion
	NORMAL_COMPLETION(0x0000),
	
	// Local node error
	LOCAL_NODE_NOT_IN_NETWORK(0x0101),
	TOKEN_TIMEOUT(0x0102),
	RETRIES_FAILED(0x0103),
	TOO_MANY_SEND_FRAMES(0x0104),
	NODE_ADDRESS_RANGE_ERROR(0x0105),
	NODE_ADDRESS_RANGE_DUPLICATION(0x0106),
	
	// Destination node error
	DESTINATION_NODE_NOT_IN_NETWORK(0x0201),
	UNIT_MISSING(0x0202),
	THIRD_NODE_MISSING(0x0203),
	DESTINATION_NODE_BUSY(0x0204),
	RESPONSE_TIMEOUT(0x0205),
	
	// Controller error
	COMMUNICATIONS_CONTROLLER_ERROR(0x0301),
	CPU_UNIT_ERROR(0x0302),
	CONTROLLER_ERROR(0x0303),
	UNIT_NUMBER_ERROR(0x0304),
	
	// Service unsupported
	UNDEFINED_COMMAND(0x0401),
	NOT_SUPPORTED_BY_MODEL_VERSION(0x0402),
	
	// Routing table error
	DESTINATION_ADDRESS_SETTING_ERROR(0x0501),
	NO_ROUTING_TABLES(0x0502),
	ROUTING_TABLE_ERROR(0x0503),
	TOO_MANY_RELAYS(0x0504),
	
	// Command format error
	COMMAND_TOO_LONG(0x1001),
	COMMAND_TOO_SHORT(0x1002),
	ELEMENTS_DATA_DONT_MATCH(0x1003),
	COMMAND_FORMAT_ERROR(0x1004),
	HEADER_ERROR(0x1005),
	
	// Parameter error
	AREA_CLASSIFICATION_MISSING(0x1101),
	ACCESS_SIZE_ERROR(0x1102),
	ADDRESS_RANGE_ERROR(0x1103),
	ADDRESS_RANGE_EXCEEDED(0x1104),
	PROGRAM_MISSING(0x1106),
	RELATIONAL_ERROR(0x1109),
	DUPLICATE_DATA_ACCESS(0x110a),
	RESPONSE_TOO_BIG(0x110b),
	PARAMETER_ERROR(0x110c),
	
	// TODO Finish entering all the end codes from Omron document W342 section 5-1-3 
	
	WRITE_NOT_POSSIBLE__READ_ONLY(0x2101),
	WRITE_NOT_POSSIBLE__PROTECTED(0x2102),
	WRITE_NOT_POSSIBLE__CANNOT_REGISTER(0x2103),
	WRITE_NOT_POSSIBLE__PROGRAM_MISSING(0x2105),
	WRITE_NOT_POSSIBLE__FILE_MISSING(0x2106),
	WRITE_NOT_POSSIBLE__FILE_NAME_ALREADY_EXISTS(0x2107),
	WRITE_NOT_POSSIBLE__CANNOT_CHANGE(0x2108),
	
	NOT_EXECUTABLE_IN_CURRENT_MODE__NOT_POSSIBLE_DURING_EXECUTION(0x2201),
	NOT_EXECUTABLE_IN_CURRENT_MODE__NOT_POSSIBLE_WHILE_RUNNING(0x2202),
	NOT_EXECUTABLE_IN_CURRENT_MODE__WRONG_PLC_MODE__IN_PROGRAM(0x2203),
	NOT_EXECUTABLE_IN_CURRENT_MODE__WRONG_PLC_MODE__IN_DEBUG(0x2204),
	NOT_EXECUTABLE_IN_CURRENT_MODE__WRONG_PLC_MODE__IN_MONITOR(0x2205),
	NOT_EXECUTABLE_IN_CURRENT_MODE__WRONG_PLC_MODE__IN_RUN(0x2206),
	NOT_EXECUTABLE_IN_CURRENT_MODE__SPECIFIED_NODE_NOT_POLLING_NODE(0x2207),
	NOT_EXECUTABLE_IN_CURRENT_MODE__STEP_CANNOT_BE_EXECUTED(0x2208),
	
	NO_SUCH_DEVICE__FILE_DEVICE_MISSING(0x2301),
	NO_SUCH_DEVICE__MEMORY_MISSING(0x2302),
	NO_SUCH_DEVICE__CLOCK_MISSING(0x2303),
	
	CANNOT_START_STOP__TABLE_MISSING(0x2401);
	
	private final short endCodeValue;

	private final static Map<Short, FinsEndCode> map = stream(FinsEndCode.values())
			.collect(toMap(endCode -> endCode.endCodeValue, endCodeValue -> endCodeValue));

	private FinsEndCode(final short endCodeValue) {
		this.endCodeValue = endCodeValue;
	}
	
	private FinsEndCode(final int endCodeValue) {
		this((short) endCodeValue);
	}

	public static Optional<FinsEndCode> valueOf(final short endCodeValue) {
		// This removes fatal and non-fatal CPU unit warning and also the relay
		// error which has nothing to do with the command response
		int mask = 0b0000_0000_0000_0000_0111_111_0011_1111;
		short val = (short) (endCodeValue & mask);
		return Optional.ofNullable(map.get(val));
	}
	
	public static Optional<FinsEndCode> valueOf(final int endCodeValue) {
		return FinsEndCode.valueOf((short) endCodeValue);
	}

	public short getValue() {
		return this.endCodeValue;
	}
	
}
