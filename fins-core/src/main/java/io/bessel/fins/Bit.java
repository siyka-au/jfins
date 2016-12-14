package io.bessel.fins;

public final class Bit {

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
	
}
