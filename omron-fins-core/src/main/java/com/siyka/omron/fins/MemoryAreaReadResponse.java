package com.siyka.omron.fins;

import java.util.List;

public abstract class MemoryAreaReadResponse<T extends DataItem<?>> implements FinsResponse {

	private final List<T> items;
	
	public MemoryAreaReadResponse(final FinsEndCode endCode, final List<T> items) {
		this.items = items;
	}
	
	@Override
	public FinsCommandCode getCommandCode() {
		return FinsCommandCode.MEMORY_AREA_READ;
	}
	
	public List<T> getItems() {
		return this.items;
	}

}
