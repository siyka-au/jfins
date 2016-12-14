package io.github.mookins.omron.fins.commands;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.github.mookins.omron.fins.FinsCommandCode;
import io.github.mookins.omron.fins.FinsEndCode;

public final class FinsMemoryAreaReadWordResponse extends FinsMemoryAreaReadResponse<Short> {

	public FinsMemoryAreaReadWordResponse(final FinsEndCode errorCode, final List<Short> items) {
		super(errorCode, items);
	}
	
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(512);
		buf.putShort(this.getCommandCode().getValue());
		buf.putShort(this.getEndCode().getValue());

		List<Short> items = this.getItems();
		buf.putShort((short) items.size());
		items.forEach((item) -> {
			buf.putShort(item);
		});
		
		buf.flip();
		byte[] bytes = new byte[buf.remaining()];
		buf.get(bytes);

		return bytes;
	}

	public static FinsMemoryAreaReadWordResponse parseFrom(final byte[] data, final short itemCount) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		FinsCommandCode commandCode = FinsCommandCode.valueOf(buf.getShort()).get();
		short endCodeRaw = buf.getShort();
		
		if ((endCodeRaw & (1L << 15)) != 0) {
		   //System.out.println("We have a network relay error, probably need to read more bytes");
		   short relayError = buf.getShort();
		   //System.out.println(String.format("Relay error 0x%04x", relayError));
		}
		
		if ((endCodeRaw & (1L << 7)) != 0) {
			   //System.out.println("We have a fatal CPU error");
		}

		if ((endCodeRaw & (1L << 6)) != 0) {
			   //System.out.println("We have a minor CPU error");
		}
		
		//System.out.println(String.format("EndCode 0x%04x", endCodeRaw));
		FinsEndCode endCode = FinsEndCode.valueOf(endCodeRaw).get();
		
		if (commandCode != FinsCommandCode.MEMORY_AREA_READ) {
			// We don't have the correct command code for the response type
		}
		
		List<Short> items = new ArrayList<>();
		
		if (endCode == FinsEndCode.NORMAL_COMPLETION) {
			for (int i = 0; i < itemCount; i++) {
				items.add(buf.getShort());
			}
		}
		
		return new FinsMemoryAreaReadWordResponse(endCode, items);
	}
	
	public static FinsMemoryAreaReadWordResponse parseFrom(final byte[] data, final int itemCount) {
		return parseFrom(data, (short) itemCount);
	}
	
}
