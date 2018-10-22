package com.siyka.omron.fins;

import java.util.List;

public abstract class MemoryAreaReadResponse<T extends DataItem<?>> extends SimpleResponse {

	private final List<T> items;
	
	public MemoryAreaReadResponse(final FinsEndCode endCode, final List<T> items) {
		super(FinsCommandCode.MEMORY_AREA_READ, endCode);
		this.items = items;
	}
	
	public List<T> getItems() {
		return this.items;
	}

}
