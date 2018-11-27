package com.siyka.omron.fins.responses;

import java.util.List;

import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.DataItem;
import com.siyka.omron.fins.EndCode;

public abstract class MemoryAreaReadResponse<T extends DataItem<?>> extends SimpleResponse {

	private final List<T> items;
	
	public MemoryAreaReadResponse(final EndCode endCode, final List<T> items) {
		super(CommandCode.MEMORY_AREA_READ, endCode);
		this.items = items;
	}
	
	public List<T> getItems() {
		return this.items;
	}

}
