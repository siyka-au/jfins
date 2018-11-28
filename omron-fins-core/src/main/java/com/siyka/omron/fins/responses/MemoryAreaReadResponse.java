package com.siyka.omron.fins.responses;

import com.siyka.omron.fins.ByteAlign;
import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.ResponseCode;

public class MemoryAreaReadResponse extends SimpleResponse {

	private final byte[] data;
	
	public MemoryAreaReadResponse(final ResponseCode endCode, final byte[] data) {
		super(CommandCode.MEMORY_AREA_READ, endCode);
		this.data = ByteAlign.align(data);
	}
	
	public byte[] getData() {
		return this.data;
	}

}
