package com.siyka.omron.fins.wip;

import java.nio.ByteBuffer;
import java.util.List;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsEndCode;

public final class MemoryAreaReadBitResponse extends MemoryAreaReadResponse<Bit> {

	public MemoryAreaReadBitResponse(final FinsEndCode errorCode, final List<Bit> items) {
		super(errorCode, items);
	}
	
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

}
