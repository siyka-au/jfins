package com.siyka.omron.fins.codec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.joou.Unsigned.ushort;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.Word;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.responses.MemoryAreaReadBitResponse;
import com.siyka.omron.fins.responses.MemoryAreaReadWordResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MemoryAreaReadCommandCodec {

	public static PayloadEncoder<MemoryAreaReadCommand> memoryAreaReadCommandEncoder = command -> {
		final ByteBuf buffer = Unpooled.buffer();
		
		buffer.writeShort(command.getCommandCode().getValue());
		buffer.writeByte(command.getIoAddress().getMemoryArea().getValue());
		buffer.writeShort(command.getIoAddress().getAddress());
		buffer.writeByte(command.getIoAddress().getBitOffset());
		buffer.writeShort(command.getItemCount());
		
		return buffer;
	};
	
	public static PayloadDecoder<MemoryAreaReadWordResponse> memoryAreaReadWordCommandDecoder(final int itemCount) {
		return buffer -> {
			final FinsCommandCode commandCode = FinsCommandCode.valueOf(buffer.readShort()).orElse(FinsCommandCode.MEMORY_AREA_READ);
			final FinsEndCode endCode = FinsEndCode.valueOf(buffer.readShort()).orElse(FinsEndCode.UNKNOWN);
			final List<Word> items = new ArrayList<>(itemCount);
			
			if (commandCode == FinsCommandCode.MEMORY_AREA_READ && endCode == FinsEndCode.NORMAL_COMPLETION) {
				IntStream.of(itemCount).forEach(i -> items.add(new Word(ushort(buffer.readShort()), false)));
			}
			
			return new MemoryAreaReadWordResponse(endCode, items);
		};
	}
	
	private static final byte BIT_VALUE_MASK = 0x01;
	private static final byte BIT_FORCED_MASK = 0x02;
	
	public static PayloadDecoder<MemoryAreaReadBitResponse> memoryAreaReadBitCommandDecoder(final int itemCount) {
		return buffer -> {
			final FinsCommandCode commandCode = FinsCommandCode.valueOf(buffer.readShort()).orElse(FinsCommandCode.MEMORY_AREA_READ);
			final FinsEndCode endCode = FinsEndCode.valueOf(buffer.readShort()).orElse(FinsEndCode.UNKNOWN);
			final List<Bit> items = new ArrayList<>(itemCount);
			
			if (commandCode == FinsCommandCode.MEMORY_AREA_READ && endCode == FinsEndCode.NORMAL_COMPLETION) {
				IntStream.of(itemCount).forEach(i -> {
					final byte bitData = buffer.readByte();
					
					final boolean value = (bitData & MemoryAreaReadCommandCodec.BIT_VALUE_MASK) != 0;
					final boolean forced = (bitData & MemoryAreaReadCommandCodec.BIT_FORCED_MASK) != 0;
					items.add(new Bit(value, forced));
				});
			}
			
			return new MemoryAreaReadBitResponse(endCode, items);
		};
	}
	
}
