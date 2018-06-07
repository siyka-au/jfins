package com.siyka.omron.fins;

import java.io.Serializable;
import java.util.Objects;

public class FinsHeader implements Serializable {

	private static final long serialVersionUID = 1253593755996704450L;
	
	private final boolean useGateway;
	private final MessageType messageType;
	private final ResponseAction responseAction;
	private final byte gatewayCount;
	private final FinsNodeAddress destinationAddress;
	private final FinsNodeAddress sourceAddress;
	private final byte serviceAddress;

	public FinsHeader(
			final MessageType messageType,
			final FinsNodeAddress destinationAddress,
			final FinsNodeAddress sourceAddress,
			final byte serviceAddress) {
		this(true, messageType, ResponseAction.RESPONSE_NOT_REQUIRED, (byte) 0x02, destinationAddress, sourceAddress, serviceAddress);
	}
	
	public FinsHeader(final boolean useGateway,
			final MessageType messageType,
			final ResponseAction responseAction,
			final byte gatewayCount,
			final FinsNodeAddress destinationAddress,
			final FinsNodeAddress sourceAddress,
			final byte serviceAddress) {
		Objects.requireNonNull(messageType);
		Objects.requireNonNull(responseAction);
		Objects.requireNonNull(destinationAddress);
		Objects.requireNonNull(sourceAddress);
		this.useGateway = useGateway;
		this.messageType = messageType;
		this.responseAction = responseAction;
		this.gatewayCount = gatewayCount;
		this.destinationAddress = destinationAddress;
		this.sourceAddress = sourceAddress;
		this.serviceAddress = serviceAddress;
	}
	
	public boolean useGateway() {
		return this.useGateway;
	}

	public MessageType getMessageType() {
		return this.messageType;
	}

	public ResponseAction getResponseAction() {
		return this.responseAction;
	}

	public byte getGatewayCount() {
		return this.gatewayCount;
	}

	public FinsNodeAddress getDestinationAddress() {
		return this.destinationAddress;
	}

	public FinsNodeAddress getSourceAddress() {
		return this.sourceAddress;
	}

	public byte getServiceAddress() {
		return this.serviceAddress;
	}
	
	public static enum MessageType {
		COMMAND, RESPONSE;
	}
	
	public static enum ResponseAction {
		RESPONSE_REQUIRED, RESPONSE_NOT_REQUIRED;
	}
	
}
