package com.siyka.omron.fins.responses;

import java.util.Collections;
import java.util.List;

import com.siyka.omron.fins.DataItem;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;

public abstract class MemoryAreaReadResponse<T extends AbstractResponse<T>, U extends DataItem<?>> extends AbstractResponse<T> {

	private final List<U> items;

	public MemoryAreaReadResponse(final FinsEndCode errorCode, final List<U> items) {
		super(FinsCommandCode.MEMORY_AREA_READ, errorCode);
		this.items = items;
	}

	public List<U> getItems() {
		return Collections.unmodifiableList(this.items);
	}
	
}
