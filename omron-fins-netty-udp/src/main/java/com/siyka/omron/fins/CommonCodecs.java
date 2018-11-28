package com.siyka.omron.fins;

import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.ResponseCode;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.responses.SimpleResponse;

import io.netty.buffer.ByteBuf;

class CommonCodecs {

	static ByteBuf encodeHeader(FinsHeader header, ByteBuf buffer) {
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
		return buffer;
	};
	
	static FinsHeader decodeHeader(ByteBuf buffer) {
		byte icf = buffer.readByte();
		// Skip the reserved field
		buffer.readByte();
		byte gatewayCount = buffer.readByte();
		byte destinationNetwork = buffer.readByte();
		byte destinationNode = buffer.readByte();
		byte destinationUnit = buffer.readByte();
		byte sourceNetwork = buffer.readByte();
		byte sourceNode = buffer.readByte();
		byte sourceUnit = buffer.readByte();
		byte serviceAddress = buffer.readByte();
		return new FinsHeader(
				((icf & 0x80) != 0) ? true : false,
				((icf & 0x40) != 0) ? FinsHeader.MessageType.RESPONSE : FinsHeader.MessageType.COMMAND,
				((icf & 0x01) != 0) ? FinsHeader.ResponseAction.RESPONSE_NOT_REQUIRED : FinsHeader.ResponseAction.RESPONSE_REQUIRED,
				gatewayCount,
				new FinsNodeAddress(destinationNetwork, destinationNode, destinationUnit),
				new FinsNodeAddress(sourceNetwork, sourceNode, sourceUnit),
				serviceAddress);
	}
	
	static ByteBuf encodeCommandCode(CommandCode commandCode, ByteBuf buffer)  {
		buffer.writeShort(commandCode.getValue());
		return buffer;
	};
	
	static CommandCode decodeCommandCode(ByteBuf buffer)  {
		return CommandCode.valueOf(buffer.readShort()).get();
	};
	
	static ByteBuf encodeResponseCode(ResponseCode endCode, ByteBuf buffer)  {
		buffer.writeShort(endCode.getValue());
		return buffer;
	};
	
	static ResponseCode decodeResponseCode(ByteBuf buffer) {
		return ResponseCode.valueOf(buffer.readShort()).get();
	}
	
	static ByteBuf encodeIoAddress(FinsIoAddress address, ByteBuf buffer)  {
		buffer.writeByte(address.getMemoryArea().getValue());
		buffer.writeShort(address.getAddress());
		buffer.writeByte(address.getBitOffset());
		return buffer;
	};
	
	static FinsIoAddress decodeIoAddress(ByteBuf buffer)  {
		FinsIoMemoryArea memoryArea = FinsIoMemoryArea.valueOf(buffer.readByte()).get();
		short address = buffer.readShort();
		byte bitOffset = buffer.readByte();
		return new FinsIoAddress(memoryArea, address, bitOffset);
	};

	static ByteBuf encodeSimpleResponse(SimpleResponse response, ByteBuf buffer) {
		encodeCommandCode(response.getCommandCode(), buffer);
		buffer.writeShort(response.getResponseCode().getValue());
		return buffer;
	}
	
}
