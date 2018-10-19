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
import com.siyka.omron.fins.FinsHeader.MessageType;
import com.siyka.omron.fins.commands.MemoryAreaWriteBitCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteDoubleWordCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteWordCommand;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.wip.MemoryAreaReadResponse;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public class FinsResponseFrameDecoder implements FinsFrameDecoder<FinsResponse> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final FinsHeaderCodec headerCodec = new FinsHeaderCodec();

	@Override
	public FinsFrame<FinsResponse> decode(final ByteBuf buffer) {
		logger.debug("Decoding FINS response frame");

		final FinsHeader header = headerCodec.decode(buffer);

		if (header.getMessageType() != MessageType.RESPONSE) {
			throw new DecoderException(
					String.format("Received message not a FINS response as determined by the header"));
		}

		final short commandCodeRaw = buffer.readShort();
		final FinsCommandCode commandCode = FinsCommandCode.valueOf(commandCodeRaw).orElseThrow(
				() -> new DecoderException(String.format("Unrecognised command code 0x%0x", commandCodeRaw)));

		switch (commandCode) {
		case CONNECTION_DATA_READ:
			break;
			
		case CPU_UNIT_DATA_READ:
			break;
			
		case CPU_UNIT_STATUS_READ:
			break;
			
		case CYCLE_TIME_READ:
			break;
			
		case MEMORY_AREA_FILL:
			break;
			
		case MEMORY_AREA_READ:
			return new FinsFrame<>(header, decodeMemoryAreaRead(buffer));

		case MEMORY_AREA_TRANSFER:
			break;
			
		case MEMORY_AREA_WRITE:
			break;
			
		case MULTIPLE_MEMORY_AREA_READ:
			break;
			
		case PARAMETER_AREA_CLEAR:
			break;
			
		case PARAMETER_AREA_READ:
			break;
			
		case PARAMETER_AREA_WRITE:
			break;
			
		case PROGRAM_AREA_CLEAR:
			break;
			
		case PROGRAM_AREA_READ:
			break;
			
		case PROGRAM_AREA_WRITE:
			break;
			
		case RUN:
			break;
			
		case STOP:
			break;
			
		default:
			break;

		}
		
		throw new UnsupportedOperationException(commandCode.name() + " decoding not supported");
	}

	private MemoryAreaReadResponse<?> decodeMemoryAreaRead(final ByteBuf buffer) {
		return null;
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
