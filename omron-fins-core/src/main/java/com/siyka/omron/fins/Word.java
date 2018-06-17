package com.siyka.omron.fins;

public class Word implements DataType<Short> {

	private static final long serialVersionUID = -2189575592046630288L;
	
	private final short data;
	
	public Word(final short data) {
		this.data = data;
	}
	
	public Short getValue() {
		return this.data;
	}
	
	public String toString() {
		return new Short(this.data).toString();
	}
	
}
