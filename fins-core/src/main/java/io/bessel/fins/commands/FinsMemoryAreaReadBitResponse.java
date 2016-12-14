package io.bessel.fins.commands;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import io.bessel.fins.Bit;
import io.bessel.fins.FinsCommandCode;
import io.bessel.fins.FinsEndCode;

public final class FinsMemoryAreaReadBitResponse extends FinsMemoryAreaReadResponse<Bit> {

	public FinsMemoryAreaReadBitResponse(final FinsEndCode errorCode, final List<Bit> items) {
		super(errorCode, items);
	}
	
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(512);
		buf.putShort(this.getCommandCode().getValue());
		buf.putShort(this.getEndCode().getValue());

		List<Bit> items = this.getItems();
		buf.putShort((short) items.size());
		items.forEach((item) -> {
			buf.put(item.getBitData());
		});
		
		buf.flip();
		byte[] bytes = new byte[buf.remaining()];
		buf.get(bytes);

		return bytes;
	}

	public static FinsMemoryAreaReadBitResponse parseFrom(final byte[] data, final short itemCount) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		FinsCommandCode commandCode = FinsCommandCode.valueOf(buf.getShort()).get();
		FinsEndCode endCode = FinsEndCode.valueOf(buf.getShort()).get();
		
		if (commandCode != FinsCommandCode.MEMORY_AREA_READ) {
			// We don't have the correct command code for the response type
		}
		
		List<Bit> items = new ArrayList<>();
		
		IntStream.range(0, itemCount).forEach((i) -> {
			byte bitData = buf.get();
			items.add(new Bit(bitData));
		});
		
		return new FinsMemoryAreaReadBitResponse(endCode, items);
	}
	
}
