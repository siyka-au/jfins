package io.github.mookins.omron.fins;

public final class FinsNodeAddress {

	private final byte address;
	private final byte node;
	private final byte unit;
	
	public FinsNodeAddress(byte address, byte node, byte unit) {
		this.address = address;
		this.node = node;
		this.unit = unit;
	}
	
	public FinsNodeAddress(int address, int node, int unit) {
		this((byte) address, (byte) node, (byte) unit);
	}

	public byte getAddress() {
		return address;
	}

	public byte getNode() {
		return node;
	}

	public byte getUnit() {
		return unit;
	}

}
