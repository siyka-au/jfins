package io.bessel.fins;

import java.nio.ByteBuffer;

public final class FinsFrameBuilder {

	private byte informationControlField;
	private byte gatewayCount;
	private byte destinationNetworkAddress;
	private byte destinationNodeNumber;
	private byte destinationUnitAddress;
	private byte sourceNetworkAddress;
	private byte sourceNodeNumber;
	private byte sourceUnitAddress;
	private byte serviceAddress;
	private byte[] data;

	public FinsFrameBuilder() {
		this.informationControlField = (byte) 0x80;
		this.serviceAddress = 0x01;
		this.gatewayCount = 0x02;
	}

	public FinsFrameBuilder setInformationControlField(byte informationControlField) {
		this.informationControlField = informationControlField;
		return this;
	}

	public FinsFrameBuilder setInformationControlField(int informationControlField) {
		this.informationControlField = (byte) informationControlField;
		return this;
	}
	
	public FinsFrameBuilder setMessageType(FinsMessageType messageType) {
		switch(messageType) {
		case COMMAND:
			this.informationControlField = (byte) (this.informationControlField & ~(1 << 6));
			break;
		case RESPONSE:
			this.informationControlField = (byte) (this.informationControlField | (1 << 6));
			break;
		}
		
		return this;
	}
	
	public FinsFrameBuilder setResponseAction(FinsResponseAction responseAction) {
		switch (responseAction) {
		case RESPONSE_REQUIRED:
			this.informationControlField = (byte) (this.informationControlField & ~1);
			break;
		case RESPONSE_NOT_REQUIRED:
			this.informationControlField = (byte) (this.informationControlField | 1);
			break;
		}
		return this;
	}
	
	public FinsFrameBuilder setGatewayCount(byte gatewayCount) {
		this.gatewayCount = gatewayCount;
		return this;
	}
	
	public FinsFrameBuilder setGatewayCount(int gatewayCount) {
		this.gatewayCount = (byte) gatewayCount;
		return this;
	}

	public FinsFrameBuilder setDestinationNetworkAddress(byte destinationNetworkAddress) {
		this.destinationNetworkAddress = destinationNetworkAddress;
		return this;
	}
	
	public FinsFrameBuilder setDestinationNetworkAddress(int destinationNetworkAddress) {
		this.destinationNetworkAddress = (byte) destinationNetworkAddress;
		return this;
	}

	public FinsFrameBuilder setDestinationNodeNumber(byte destinationNodeNumber) {
		this.destinationNodeNumber = destinationNodeNumber;
		return this;
	}
	
	public FinsFrameBuilder setDestinationNodeNumber(int destinationNodeNumber) {
		this.destinationNodeNumber = (byte) destinationNodeNumber;
		return this;
	}

	public FinsFrameBuilder setDestinationUnitAddress(byte destinationUnitAddress) {
		this.destinationUnitAddress = destinationUnitAddress;
		return this;
	}
	
	public FinsFrameBuilder setDestinationUnitAddress(int destinationUnitAddress) {
		this.destinationUnitAddress = (byte) destinationUnitAddress;
		return this;
	}
	
	public FinsFrameBuilder setDestinationAddress(int networkAddress, int nodeNumber, int unitAddress) {
		this.setDestinationNetworkAddress(networkAddress);
		this.setDestinationNodeNumber(nodeNumber);
		this.setDestinationUnitAddress(unitAddress);
		return this;
	}

	public FinsFrameBuilder setDestinationAddress(FinsNodeAddress destination) {
		this.setDestinationAddress(destination.getAddress(), destination.getNode(), destination.getUnit());
		return this;
	}
	
	public FinsFrameBuilder setSourceNetworkAddress(byte sourceNetworkAddress) {
		this.sourceNetworkAddress = sourceNetworkAddress;
		return this;
	}
	
	public FinsFrameBuilder setSourceNetworkAddress(int sourceNetworkAddress) {
		this.sourceNetworkAddress = (byte) sourceNetworkAddress;
		return this;
	}

	public FinsFrameBuilder setSourceNodeNumber(byte sourceNodeNumber) {
		this.sourceNodeNumber = sourceNodeNumber;
		return this;
	}

	public FinsFrameBuilder setSourceNodeNumber(int sourceNodeNumber) {
		this.sourceNodeNumber = (byte) sourceNodeNumber;
		return this;
	}
	
	public FinsFrameBuilder setSourceUnitAddress(byte sourceUnitAddress) {
		this.sourceUnitAddress = sourceUnitAddress;
		return this;
	}
	
	public FinsFrameBuilder setSourceUnitAddress(int sourceUnitAddress) {
		this.sourceUnitAddress = (byte) sourceUnitAddress;
		return this;
	}
	
	public FinsFrameBuilder setSourceAddress(FinsNodeAddress source) {
		this.setSourceAddress(source.getAddress(), source.getNode(), source.getUnit());
		return this;
	}
	
	public FinsFrameBuilder setSourceAddress(int networkAddress, int nodeNumber, int unitAddress) {
		this.setSourceNetworkAddress(networkAddress);
		this.setSourceNodeNumber(nodeNumber);
		this.setSourceUnitAddress(unitAddress);
		return this;
	}

	public FinsFrameBuilder setServiceAddress(byte serviceAddress) {
		this.serviceAddress = serviceAddress;
		return this;
	}
	
	public FinsFrameBuilder setServiceAddress(int serviceAddress) {
		this.serviceAddress = (byte) serviceAddress;
		return this;
	}

	public FinsFrameBuilder setData(byte[] data) {
		this.data = data;
		return this;
	}

	public FinsFrame build() {
		return new FinsFrame(this.informationControlField, this.gatewayCount, this.destinationNetworkAddress,
				this.destinationNodeNumber, this.destinationUnitAddress, this.sourceNetworkAddress,
				this.sourceNodeNumber, this.sourceUnitAddress, this.serviceAddress, this.data);
	}
	
	public static FinsFrame parseFrom(byte[] frameBytes) throws FinsFrameException {
		ByteBuffer buf = ByteBuffer.wrap(frameBytes);

		if (buf.remaining() < 10)
			throw new FinsFrameException("Insufficient (10 bytes) data for the FINS frame header");

		// Read in the header
		byte informationControlField = buf.get();
		// Skip the reserved field
		buf.get();
		byte gatewayCount = buf.get();
		byte destinationNetworkAddress = buf.get();
		byte destinationNodeNumber = buf.get();
		byte destinationUnitAddress = buf.get();
		byte sourceNetworkAddress = buf.get();
		byte sourceNodeNumber = buf.get();
		byte sourceUnitAddress = buf.get();
		byte serviceAddress = buf.get();

		// Read in the data
		byte[] data = new byte[buf.remaining()];
		buf.get(data);

		FinsFrame finsFrame = new FinsFrameBuilder()
				.setInformationControlField(informationControlField)
				.setGatewayCount(gatewayCount)
				.setDestinationNetworkAddress(destinationNetworkAddress)
				.setDestinationNodeNumber(destinationNodeNumber)
				.setDestinationUnitAddress(destinationUnitAddress)
				.setSourceNetworkAddress(sourceNetworkAddress)
				.setSourceNodeNumber(sourceNodeNumber)
				.setSourceUnitAddress(sourceUnitAddress)
				.setServiceAddress(serviceAddress)
				.setData(data)
				.build();

		return finsFrame;
	}
	
	public static FinsFrameBuilder builderFromPrototype(FinsFrame finsFrame) {
		return new FinsFrameBuilder()
				.setInformationControlField(finsFrame.getInformationControlField())
				.setGatewayCount(finsFrame.getGatewayCount())
				.setDestinationNetworkAddress(finsFrame.getDestinationNetworkAddress())
				.setDestinationNodeNumber(finsFrame.getDestinationNodeNumber())
				.setDestinationUnitAddress(finsFrame.getDestinationUnitAddress())
				.setSourceNetworkAddress(finsFrame.getSourceNetworkAddress())
				.setSourceNodeNumber(finsFrame.getSourceNodeNumber())
				.setSourceUnitAddress(finsFrame.getSourceUnitAddress())
				.setServiceAddress(finsFrame.getServiceAddress())
				.setData(finsFrame.getData());
	}
	
}
