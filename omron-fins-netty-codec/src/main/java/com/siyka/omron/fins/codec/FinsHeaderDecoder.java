package com.siyka.omron.fins.codec;

import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.NodeAddress;

import io.netty.buffer.ByteBuf;

public class FinsHeaderDecoder implements Decoder<FinsHeader> {
	
	public FinsHeader decode(final ByteBuf buffer) {
		final byte icf = buffer.readByte();
		// Skip the reserved field
		buffer.readByte();
		final byte gatewayCount = buffer.readByte();
		final byte destinationNetwork = buffer.readByte();
		final byte destinationNode = buffer.readByte();
		final byte destinationUnit = buffer.readByte();
		final byte sourceNetwork = buffer.readByte();
		final byte sourceNode = buffer.readByte();
		final byte sourceUnit = buffer.readByte();
		final byte serviceAddress = buffer.readByte();
		return new FinsHeader(
				((icf & 0x80) != 0) ? true : false,
				((icf & 0x40) != 0) ? FinsHeader.MessageType.RESPONSE : FinsHeader.MessageType.COMMAND,
				((icf & 0x01) != 0) ? FinsHeader.ResponseAction.RESPONSE_NOT_REQUIRED : FinsHeader.ResponseAction.RESPONSE_REQUIRED,
				gatewayCount,
				new NodeAddress(destinationNetwork, destinationNode, destinationUnit),
				new NodeAddress(sourceNetwork, sourceNode, sourceUnit),
				serviceAddress);
	}
	
}
