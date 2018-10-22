package com.siyka.omron.fins;

import java.util.Objects;

public class FinsFrame<T extends FinsPdu> {

	private final FinsHeader header;
	private final T pdu;

	public FinsFrame(final FinsHeader header, final T pdu) {
		Objects.requireNonNull(header);
		Objects.requireNonNull(pdu);
		this.header = header;
		this.pdu = pdu;
	}
	
	public FinsHeader getHeader() {
		return this.header;
	}

	public T getPdu() {
		return this.pdu;
	}

	public String toString() {
		return String.format("FINS type[%s] dst[%02x-%02x-%02x] src[%02x-%02x-%02x] svc[0x%02x] pdu[%s]",
				this.getHeader().getMessageType(),
				this.getHeader().getDestinationAddress().getNetwork(),
				this.getHeader().getDestinationAddress().getNode(),
				this.getHeader().getDestinationAddress().getUnit(),
				this.getHeader().getSourceAddress().getNetwork(),
				this.getHeader().getSourceAddress().getNode(),
				this.getHeader().getSourceAddress().getUnit(),
				this.getHeader().getServiceAddress(),
				this.getPdu());
	}
	
}
