package com.siyka.omron.fins;

import java.io.Serializable;

public final class FinsNodeAddress implements Serializable {

	private static final long serialVersionUID = -675204641052490639L;
	
	private final byte network;
	private final byte node;
	private final byte unit;

	public FinsNodeAddress(final byte network, final byte node, final byte unit) {
		this.network = network;
		this.node = node;
		this.unit = unit;
	}

	public FinsNodeAddress(final int network, final int node, final int unit) {
		this((byte) network, (byte) node, (byte) unit);
	}

	public byte getNetwork() {
		return this.network;
	}

	public byte getNode() {
		return this.node;
	}

	public byte getUnit() {
		return this.unit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + network;
		result = prime * result + node;
		result = prime * result + unit;
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
		FinsNodeAddress other = (FinsNodeAddress) obj;
		if (network != other.network)
			return false;
		if (node != other.node)
			return false;
		if (unit != other.unit)
			return false;
		return true;
	}

}
