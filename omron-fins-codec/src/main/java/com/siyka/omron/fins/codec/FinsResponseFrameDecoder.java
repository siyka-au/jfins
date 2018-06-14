package com.siyka.omron.fins.codec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsIoMemoryArea;
import com.siyka.omron.fins.responses.FinsSimpleResponse;
import com.siyka.omron.fins.responses.MemoryAreaReadResponse;
import com.siyka.omron.fins.responses.MemoryAreaReadWordResponse;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public class FinsResponseFrameDecoder implements FinsFrameDecoder {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public FinsFrame decode(final ByteBuf buffer) {
		final FinsHeader header = FinsHeaderCodec.decode(buffer);
		
		final short commandCodeRaw = buffer.readShort();
		final FinsCommandCode commandCode = FinsCommandCode.valueOf(commandCodeRaw)
				.orElseThrow(() -> new DecoderException(String.format("Unrecognised command code 0x%0x", commandCodeRaw)));
		
		final short endCodeRaw = buffer.readShort();
		final FinsEndCode endCode = FinsEndCode.valueOf(endCodeRaw)
				.orElseThrow(() -> new DecoderException(String.format("Unrecognised end code 0x%0x", endCodeRaw)));
		
		switch (commandCode) {
			case MEMORY_AREA_READ:
				return new FinsFrame(header, decodeMemoryAreaReadResponse(commandCode, endCode, buffer));
		
			case MEMORY_AREA_WRITE:
			case MEMORY_AREA_FILL:
			case MEMORY_AREA_TRANSFER:
			case PARAMETER_AREA_WRITE:
			case PROGRAM_AREA_CLEAR:
			case RUN:
			case STOP:
				return new FinsFrame(header, new FinsSimpleResponse(commandCode, endCode));

			default:
				throw new DecoderException(String.format("Command code not implemented or supported: %s", commandCode));
		}
	}
	
	private FinsIoAddress decodeIoAddress(final ByteBuf buffer) {
		final byte ioMemoryAreaCode = buffer.readByte();
		final FinsIoMemoryArea memoryAreaCode =  FinsIoMemoryArea.valueOf(ioMemoryAreaCode)
				.orElseThrow(() -> new DecoderException(String.format("Unrecognised IO memory area code 0x%x", ioMemoryAreaCode)));
		final short address = buffer.readShort();
		final byte bitOffset = buffer.readByte();
		return new FinsIoAddress(memoryAreaCode, address, bitOffset);
	}
	
	private MemoryAreaReadResponse<?> decodeMemoryAreaReadResponse(final FinsCommandCode commandCode, final FinsEndCode endCode, final ByteBuf buffer) {
		final short itemCount = buffer.readShort();
		final List<Short> items = new ArrayList<>();

//		switch (ioAddress.getMemoryArea().getDataType()) {
//			case WORD:
				IntStream.range(0, itemCount)
						.forEach(i -> items.add(buffer.readShort()));
				return new MemoryAreaReadWordResponse(commandCode, endCode, items);
				
//			default:
//		}
		
//		return null;
	}
	
}
