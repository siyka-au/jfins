package com.siyka.omron.fins.codec;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsIoMemoryArea;
import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.commands.MemoryAreaWriteBitCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteDoubleWordCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteWordCommand;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public class FinsCommandFrameDecoder implements FinsFrameDecoder {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public FinsFrame decode(final ByteBuf buffer) throws DecoderException {
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
		final FinsHeader header = new FinsHeader(
				((icf & 0x80) != 0) ? true : false,
				((icf & 0x40) != 0) ? FinsHeader.MessageType.RESPONSE : FinsHeader.MessageType.COMMAND,
				((icf & 0x01) != 0) ? FinsHeader.ResponseAction.RESPONSE_NOT_REQUIRED : FinsHeader.ResponseAction.RESPONSE_REQUIRED,
				gatewayCount,
				new FinsNodeAddress(destinationNetwork, destinationNode, destinationUnit),
				new FinsNodeAddress(sourceNetwork, sourceNode, sourceUnit),
				serviceAddress);
		
		final short commandCodeRaw = buffer.readShort();
		final FinsCommandCode commandCode = FinsCommandCode.valueOf(commandCodeRaw)
				.orElseThrow(() -> new DecoderException(String.format("Unrecognised command code 0x%0x", commandCodeRaw)));
		
		switch (commandCode) {
			case MEMORY_AREA_WRITE:
				return new FinsFrame(header, decodeMemoryAreaWrite(commandCode, buffer));

			default:
				throw new DecoderException(String.format("Command code not implemented or supported", commandCode));
		}
	}
	
	private MemoryAreaWriteCommand<?> decodeMemoryAreaWrite(final FinsCommandCode commandCode, final ByteBuf buffer) throws DecoderException {
		final byte ioMemoryAreaCode = buffer.readByte();
		final FinsIoMemoryArea memoryAreaCode = FinsIoMemoryArea.valueOf(ioMemoryAreaCode)
				.orElseThrow(() -> new DecoderException(String.format("Unrecognised IO memory area code 0x%x", ioMemoryAreaCode)));
		final short address = buffer.readShort();
		final byte bitOffset = buffer.readByte();
		final short dataItemCount = buffer.readShort();
		final FinsIoAddress ioAddress = new FinsIoAddress(memoryAreaCode, address, bitOffset);
		switch (memoryAreaCode.getDataByteSize()) {
			case 1: {
				// Bit data
				final List<Bit> dataItems = new ArrayList<>(dataItemCount);
				for (int i = 0; i < dataItemCount; i++) {
					// @Todo
					//dataItems.add(buffer.readShort());
				}
				return new MemoryAreaWriteBitCommand(commandCode, ioAddress, dataItems);
			}
				
			case 2: {
				// Word data
				final List<Short> dataItems = new ArrayList<>(dataItemCount);
				for (int i = 0; i < dataItemCount; i++) {
					dataItems.add(buffer.readShort());
				}
				return new MemoryAreaWriteWordCommand(commandCode, ioAddress, dataItems);
			}
				
			case 4: {
				// Double word data
				final List<Integer> dataItems = new ArrayList<>(dataItemCount);
				for (int i = 0; i < dataItemCount; i++) {
					dataItems.add(buffer.readInt());
				}
				return new MemoryAreaWriteDoubleWordCommand(commandCode, ioAddress, dataItems);
			}
				
			default:
				return null;
		}
	}
	
}
