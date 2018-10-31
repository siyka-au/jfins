package com.siyka.omron.fins;

import java.io.Serializable;

public final class FinsIoAddress implements Serializable {

	private static final long serialVersionUID = 256655711718324689L;
	
	private final FinsIoMemoryArea memoryArea;
	private final short address;
	private final byte bitOffset;

	public FinsIoAddress(final FinsIoMemoryArea memoryArea, final short address) {
		this(memoryArea, address, (byte) 0);
	}
	
	public FinsIoAddress(final FinsIoMemoryArea memoryArea, final int address) {
		this(memoryArea, (short) address);
	}
	
	public FinsIoAddress(final FinsIoMemoryArea memoryArea, final int address, final int bitOffset) {
		this(memoryArea, (short) address, (byte) bitOffset);
	}
	
	public FinsIoAddress(final FinsIoMemoryArea memoryArea, final short address, final int bitOffset) {
		this(memoryArea, address, (byte) bitOffset);
	}
	
	public FinsIoAddress(final FinsIoMemoryArea memoryArea, final short address, final byte bitOffset) {
		super();
		this.memoryArea = memoryArea;
		this.address = address;
		this.bitOffset = bitOffset;
	}

	public FinsIoMemoryArea getMemoryArea() {
		return this.memoryArea;
	}

	public short getAddress() {
		return this.address;
	}

	public byte getBitOffset() {
		return this.bitOffset;
	}
	
	public String toString() {
		return String.format("FINS IO ADDR[%d.%d]",
				this.getAddress(),
				this.getBitOffset());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + address;
		result = prime * result + bitOffset;
		result = prime * result + ((memoryArea == null) ? 0 : memoryArea.hashCode());
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
		FinsIoAddress other = (FinsIoAddress) obj;
		if (address != other.address)
			return false;
		if (bitOffset != other.bitOffset)
			return false;
		if (memoryArea != other.memoryArea)
			return false;
		return true;
	}
	
	public static FinsIoAddress dmWord(int address) {
		return new FinsIoAddress(FinsIoMemoryArea.DM_WORD, address);
	}
	
}
