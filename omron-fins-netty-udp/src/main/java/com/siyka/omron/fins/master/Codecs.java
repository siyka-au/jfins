package com.siyka.omron.fins.master;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.Word;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.responses.SimpleResponse;

import io.netty.buffer.ByteBuf;

public class Codecs {

	public static ByteBuf encodeHeader(final ByteBuf buffer, final FinsHeader header) {
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
	
	public static FinsHeader decodeHeader(final ByteBuf buffer) {
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
	
	static ByteBuf encodeCommandCode(final ByteBuf buffer, final CommandCode commandCode)  {
		buffer.writeShort(commandCode.getValue());
		return buffer;
	};
	
	public static CommandCode decodeCommandCode(final ByteBuf buffer)  {
		return CommandCode.valueOf(buffer.readShort()).orElse(CommandCode.UNKNOWN);
	};
	
	static ByteBuf encodeIoAddress(final ByteBuf buffer, final FinsIoAddress address)  {
		buffer.writeByte(address.getMemoryArea().getValue());
		buffer.writeShort(address.getAddress());
		buffer.writeByte(address.getBitOffset());
		return buffer;
	};

	public static List<Word> decodeMemoryAreaReadWordsResponse(final ByteBuf buffer, final MemoryAreaReadCommand initiatingCommand) {
		final List<Word> items = new ArrayList<>(initiatingCommand.getItemCount());
		IntStream.range(0, initiatingCommand.getItemCount()).forEach(i -> items.add(new Word(buffer.readShort(), false)));
		return items;
	};

	public static ByteBuf encodeSimpleResponse(final ByteBuf buffer, final SimpleResponse command) {
		// TODO Auto-generated method stub
		return buffer;
	}
	
}
