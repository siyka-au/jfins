package com.siyka.omron.fins.responses;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;

public abstract class MemoryAreaReadResponse<T> extends FinsSimpleResponse {

	private final List<T> items;
	
	public MemoryAreaReadResponse(final FinsCommandCode commandCode, final FinsEndCode endCode, final List<T> items) {
		super(commandCode, endCode);
		Objects.requireNonNull(items);
		if (items.getClass().getSimpleName().equals("UnmodifiableCollection")) {
			this.items = items;
		} else {
			this.items = Collections.unmodifiableList(items);
		}
	}

	public List<T> getItems() {
		return items;
	}
	
	public String toString() {
		return String.format("%s itemCount[%d]",
				super.toString(),
				this.items.size());
	}
	
}
