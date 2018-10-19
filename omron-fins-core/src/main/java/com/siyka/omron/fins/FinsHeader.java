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
	
	public static class Builder {
		
		private boolean useGateway;
		private MessageType messageType;
		private ResponseAction responseAction;
		private byte gatewayCount;
		private FinsNodeAddress destinationAddress;
		private FinsNodeAddress sourceAddress;
		private byte serviceAddress;
		
		public Builder() {
			this.useGateway = true;
			this.gatewayCount = 0x02;
		}

		public boolean isUseGateway() {
			return useGateway;
		}

		public Builder setUseGateway(boolean useGateway) {
			this.useGateway = useGateway;
			return this;
		}

		public MessageType getMessageType() {
			return messageType;
		}

		public Builder setMessageType(MessageType messageType) {
			this.messageType = messageType;
			return this;
		}

		public ResponseAction getResponseAction() {
			return responseAction;
		}

		public Builder setResponseAction(ResponseAction responseAction) {
			this.responseAction = responseAction;
			return this;
		}

		public byte getGatewayCount() {
			return gatewayCount;
		}

		public Builder setGatewayCount(byte gatewayCount) {
			this.gatewayCount = gatewayCount;
			return this;
		}

		public FinsNodeAddress getDestinationAddress() {
			return destinationAddress;
		}

		public Builder setDestinationAddress(FinsNodeAddress destinationAddress) {
			this.destinationAddress = destinationAddress;
			return this;
		}

		public FinsNodeAddress getSourceAddress() {
			return sourceAddress;
		}

		public Builder setSourceAddress(FinsNodeAddress sourceAddress) {
			this.sourceAddress = sourceAddress;
			return this;
		}

		public byte getServiceAddress() {
			return serviceAddress;
		}

		public Builder setServiceAddress(byte serviceAddress) {
			this.serviceAddress = serviceAddress;
			return this;
		}
		
		public FinsHeader build() {
			return new FinsHeader(this.useGateway, this.messageType, this.responseAction, this.gatewayCount, this.destinationAddress, this.sourceAddress, this.serviceAddress);
		}
		
		public static Builder defaultCommandBuilder() {
			final Builder builder = new Builder();
			builder.setMessageType(MessageType.COMMAND);
			builder.setResponseAction(ResponseAction.RESPONSE_REQUIRED);
			return builder;
		}
		
		public static Builder defaultResponseBuilder() {
			final Builder builder = new Builder();
			builder.setMessageType(MessageType.RESPONSE);
			builder.setResponseAction(ResponseAction.RESPONSE_NOT_REQUIRED);
			return builder;
		}
		
	}
	
}
