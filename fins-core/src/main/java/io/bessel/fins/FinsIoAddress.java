package io.bessel.fins;

import java.nio.ByteBuffer;

public final class FinsIoAddress {

	private final FinsIoMemoryArea memoryArea;
	private final short address;
	private final byte bitOffset;

	public FinsIoAddress(FinsIoMemoryArea memoryArea, short address) {
		this(memoryArea, address, (byte) 0);
	}
	
	public FinsIoAddress(FinsIoMemoryArea memoryArea, int address) {
		this(memoryArea, (short) address);
	}
	
	public FinsIoAddress(FinsIoMemoryArea memoryArea, int address, int bitOffset) {
		this(memoryArea, (short) address, (byte) bitOffset);
	}
	
	public FinsIoAddress(FinsIoMemoryArea memoryArea, short address, int bitOffset) {
		this(memoryArea, address, (byte) bitOffset);
	}
	
	public FinsIoAddress(FinsIoMemoryArea memoryArea, short address, byte bitOffset) {
		super();
		this.memoryArea = memoryArea;
		this.address = address;
		this.bitOffset = bitOffset;
	}

	public FinsIoMemoryArea getMemoryArea() {
		return memoryArea;
	}

	public short getAddress() {
		return address;
	}

	public byte getBitOffset() {
		return bitOffset;
	}
	
	public static FinsIoAddress parseFrom(final int addressOrdinal) {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putInt(addressOrdinal);
		buf.flip();
		FinsIoMemoryArea memoryAreaCode = FinsIoMemoryArea.valueOf(buf.get()).get();
		short address = buf.getShort();
		byte bitOffset = buf.get();
		
		return new FinsIoAddress(memoryAreaCode, address, bitOffset);
	}
	
}
