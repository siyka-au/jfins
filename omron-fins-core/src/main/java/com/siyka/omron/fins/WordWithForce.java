package com.siyka.omron.fins;

public class WordWithForce extends Word implements Forceable<Short> {

	private static final long serialVersionUID = -2189575592046630288L;
	
	private final short forceData;
	
	public WordWithForce(final short data, final short forceData) {
		super(data);
		this.forceData = forceData;
	}
	
	@Override
	public Short getForceMask() {
		return this.forceData;
	}
	
	public String toString() {
		return String.format("%d(%d)",
				this.getValue(),
				this.forceData);
	}

}
