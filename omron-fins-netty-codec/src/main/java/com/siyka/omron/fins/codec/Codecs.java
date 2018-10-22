package com.siyka.omron.fins.codec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.MemoryAreaReadCommand;
import com.siyka.omron.fins.MemoryAreaWriteWordsCommand;
import com.siyka.omron.fins.Word;

import io.netty.buffer.ByteBuf;

public class Codecs {

	public static void encodeHeader(final ByteBuf buffer, final FinsHeader header) {
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
	
	private static void encodeCommandCode(final ByteBuf buffer, final FinsCommandCode commandCode)  {
		buffer.writeShort(commandCode.getValue());
	};
	
	public static FinsCommandCode decodeCommandCode(final ByteBuf buffer)  {
		return FinsCommandCode.valueOf(buffer.readShort()).orElse(FinsCommandCode.UNKNOWN);
	};
	
	private static void encodeIoAddress(final ByteBuf buffer, final FinsIoAddress address)  {
		buffer.writeByte(address.getMemoryArea().getValue());
		buffer.writeShort(address.getAddress());
		buffer.writeByte(address.getBitOffset());
	};
	
	public static void encodeMemoryAreaReadCommand(final ByteBuf buffer, final MemoryAreaReadCommand command)  {
		encodeCommandCode(buffer, command.getCommandCode());
		encodeIoAddress(buffer, command.getAddress());
		buffer.writeShort(command.getItemCount());
	}

	public static List<Word> decodeMemoryAreaReadWordsResponse(final ByteBuf buffer, final MemoryAreaReadCommand initiatingCommand) {
		final List<Word> items = new ArrayList<>(initiatingCommand.getItemCount());
		IntStream.range(0, initiatingCommand.getItemCount()).forEach(i -> items.add(new Word(buffer.readShort(), false)));
		return items;
	};
	
	public static void encodeMemoryAreaWriteWordsCommand(final ByteBuf buffer, final MemoryAreaWriteWordsCommand command) {
		encodeCommandCode(buffer, command.getCommandCode());
		encodeIoAddress(buffer, command.getAddress());
		buffer.writeShort(command.getItems().size());
		command.getItems().forEach(word -> buffer.writeShort(word.getValue()));
	}
	
}
