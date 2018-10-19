package com.siyka.omron.fins.codec;

import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsNodeAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class FinsHeaderCodec {

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
				new FinsNodeAddress(destinationNetwork, destinationNode, destinationUnit),
				new FinsNodeAddress(sourceNetwork, sourceNode, sourceUnit),
				serviceAddress);
	}
	
	public void encode(final ByteBuf buffer, final FinsHeader header) {
		byte icf = 0x00;
		if (header.useGateway() == true) icf = (byte) (icf | 0x80);
		if (header.getMessageType() == FinsHeader.MessageType.RESPONSE) icf = (byte) (icf | 0x40);
		if (header.getResponseAction() == FinsHeader.ResponseAction.RESPONSE_NOT_REQUIRED) icf = (byte) (icf | 0x01);
		buffer.writeByte(icf);
		buffer.writeByte(0x00);
		buffer.writeByte(header.getGatewayCount());
		buffer.writeByte(header.getDestinationAddress().getNetwork());
		buffer.writeByte(header.getDestinationAddress().getNode());
		buffer.writeByte(header.getDestinationAddress().getUnit());
		buffer.writeByte(header.getSourceAddress().getNetwork());
		buffer.writeByte(header.getSourceAddress().getNode());
		buffer.writeByte(header.getSourceAddress().getUnit());
		buffer.writeByte(header.getServiceAddress());
	}
	
	public ByteBuf encode(final FinsHeader header) {
		final ByteBuf buffer = Unpooled.buffer();
		encode(buffer, header);
		return buffer;
	}
	
}
