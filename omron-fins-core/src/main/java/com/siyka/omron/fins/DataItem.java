package com.siyka.omron.fins;

public abstract class DataItem<T> {

	private final T value;
	private final boolean forced;

	public DataItem(final T value, final boolean forced) {
		this.value = value;
		this.forced = forced;
	}

	public T getValue() {
		return this.value;
	}
	
	public boolean isForced() {
		return this.forced;
	}

}
