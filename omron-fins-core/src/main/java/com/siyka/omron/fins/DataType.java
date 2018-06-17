package com.siyka.omron.fins;

import java.io.Serializable;

public interface DataType<T> extends Serializable {

	public T getValue();
	
}
