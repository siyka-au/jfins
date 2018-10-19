package com.siyka.omron.fins;

import java.io.Serializable;

public class Bit extends DataItem<Boolean> implements Serializable {

	private static final long serialVersionUID = -1706234047187405546L;
	
	public Bit(Boolean value, boolean forced) {
		super(value, forced);
	}

}
