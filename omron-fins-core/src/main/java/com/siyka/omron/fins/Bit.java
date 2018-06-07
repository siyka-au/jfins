package com.siyka.omron.fins;

import java.io.Serializable;

public final class Bit implements Serializable {

	private static final long serialVersionUID = -1706234047187405546L;
	
	private static final byte VALUE_MASK = 0x01;
	private static final byte FORCED_MASK = 0x02;
	
	private final byte bitData;

	public Bit(byte bitData) {
		this.bitData = bitData;
	}

	public boolean getValue() {
		return ((this.bitData & VALUE_MASK) != 0);
	}
	
	public boolean isForced() {
		return ((this.bitData & FORCED_MASK) != 0);
	}

	public byte getBitData() {
		return this.bitData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bitData;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bit other = (Bit) obj;
		if (bitData != other.bitData)
			return false;
		return true;
	}
	
}
