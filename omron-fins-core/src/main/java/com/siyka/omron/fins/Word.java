package com.siyka.omron.fins;

import java.io.Serializable;

public class Word extends DataItem<Short> implements Serializable {

	private static final long serialVersionUID = 40543402839825522L;

	public Word(final Short value, final boolean forced) {
		super(value, forced);
	}

}
