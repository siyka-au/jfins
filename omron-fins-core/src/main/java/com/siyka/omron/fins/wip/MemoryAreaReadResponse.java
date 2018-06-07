package com.siyka.omron.fins.wip;

import java.util.Collections;
import java.util.List;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.responses.FinsSimpleResponse;

public abstract class MemoryAreaReadResponse<T> extends FinsSimpleResponse {

	private final List<T> items;

	public MemoryAreaReadResponse(final FinsEndCode errorCode, final List<T> items) {
		super(FinsCommandCode.MEMORY_AREA_READ, errorCode);
		this.items = items;
	}

	public List<T> getItems() {
		return Collections.unmodifiableList(this.items);
	}
	
}
