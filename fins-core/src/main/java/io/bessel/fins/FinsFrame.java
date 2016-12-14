package io.bessel.fins;

import java.nio.ByteBuffer;

import io.bessel.fins.util.StringUtilities;

public class FinsFrame {

	private final byte informationControlField;
	private final byte reserved = 0x00;
	private final byte gatewayCount = 0x02;
	private final byte destinationNetworkAddress;
	private final byte destinationNodeNumber;
	private final byte destinationUnitAddress;
	private final byte sourceNetworkAddress;
	private final byte sourceNodeNumber;
	private final byte sourceUnitAddress;
	private final byte serviceAddress;
	private final byte[] data;

	public FinsFrame(byte informationControlField, byte gateywayCount, byte destinationNetworkAddress,
			byte destinationNodeNumber, byte destinationUnitAddress, byte sourceNetworkAddress, byte sourceNodeNumber,
			byte sourceUnitAddress, byte serviceAddress, byte[] data) {
		this.informationControlField = informationControlField;
		this.destinationNetworkAddress = destinationNetworkAddress;
		this.destinationNodeNumber = destinationNodeNumber;
		this.destinationUnitAddress = destinationUnitAddress;
		this.sourceNetworkAddress = sourceNetworkAddress;
		this.sourceNodeNumber = sourceNodeNumber;
		this.sourceUnitAddress = sourceUnitAddress;
		this.serviceAddress = serviceAddress;
		this.data = data;
	}

	public byte getInformationControlField() {
		return informationControlField;
	}

	public byte getReserved() {
		return reserved;
	}

	public byte getGatewayCount() {
		return gatewayCount;
	}

	public FinsNodeAddress getDestinationAddress() {
		return new FinsNodeAddress(this.getDestinationNetworkAddress(), this.getDestinationNodeNumber(), this.getDestinationUnitAddress());
	}
	
	public byte getDestinationNetworkAddress() {
		return destinationNetworkAddress;
	}

	public byte getDestinationNodeNumber() {
		return destinationNodeNumber;
	}

	public byte getDestinationUnitAddress() {
		return destinationUnitAddress;
	}

	public FinsNodeAddress getSourceAddress() {
		return new FinsNodeAddress(this.getSourceNetworkAddress(), this.getSourceNodeNumber(), this.getSourceUnitAddress());
	}
	
	public byte getSourceNetworkAddress() {
		return sourceNetworkAddress;
	}

	public byte getSourceNodeNumber() {
		return sourceNodeNumber;
	}

	public byte getSourceUnitAddress() {
		return sourceUnitAddress;
	}

	public byte getServiceAddress() {
		return serviceAddress;
	}

	public byte[] getData() {
		return data;
	}
	
	public boolean isCommand() {
		return (this.informationControlField & 0b01000000) == 0;
	}
	
	public boolean isResponse() {
		return (this.informationControlField & 0b01000000) != 0;
	}
	
	public boolean isResponseRequired() {
		return (this.informationControlField & 0b00000001) == 0;
	}

	public byte[] toByteArray() {
		int frameLength = 10 + this.getData().length;
		byte[] data = new byte[frameLength];
		
		ByteBuffer buf = ByteBuffer.allocate(frameLength);
		buf.put(this.getInformationControlField())
			.put((byte) 0x0) // reserved byte
			.put(this.getGatewayCount())
			.put(this.getDestinationNetworkAddress())
			.put(this.getDestinationNodeNumber())
			.put(this.getDestinationUnitAddress())
			.put(this.getSourceNetworkAddress())
			.put(this.getSourceNodeNumber())
			.put(this.getSourceUnitAddress())
			.put(this.getServiceAddress())
			.put(this.getData())
			.flip();
		buf.get(data);
		
		return data;
	}
	
	public String toString() {
		return String.format("FINS dst[%02x-%02x-%02x] src[%02x-%02x-%02x] svc[0x%02x] data[%s]",
				this.getDestinationNetworkAddress(),
				this.getDestinationNodeNumber(),
				this.getDestinationUnitAddress(),
				this.getSourceNetworkAddress(),
				this.getSourceNodeNumber(),
				this.getSourceUnitAddress(),
				this.getServiceAddress(),
				StringUtilities.getHexString(this.getData()));
	}
	
}
