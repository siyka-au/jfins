package io.bessel.fins.commands;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.bessel.fins.FinsCommandCode;
import io.bessel.fins.FinsIoAddress;

public class FinsMemoryAreaWriteBitCommand extends FinsMemoryAreaWriteCommand {

	private final Boolean value;

	public FinsMemoryAreaWriteBitCommand(final FinsIoAddress address, final Boolean value) {
		super(FinsCommandCode.MEMORY_AREA_WRITE, address);
		this.value = value;
	}
	
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(512);
		buf.putShort(this.getCommandCode().getValue());
		buf.put(this.getIoAddress().getMemoryArea().getValue());
		buf.putShort(this.getIoAddress().getAddress());
		buf.put(this.getIoAddress().getBitOffset());
		
		buf.putShort((short) 1);
		byte writeValue = this.value ? (byte) 1 : (byte) 0;
		buf.put((byte) writeValue);

		buf.flip();
		byte[] bytes = new byte[buf.remaining()];
		buf.get(bytes);

		return bytes;
	}
	
//	public static class Builder {
//
//		public static FinsMemoryAreaWriteBitCommand parseFrom(byte[] commandPayload) {
//			ByteBuffer buf = ByteBuffer.wrap(commandPayload);
//
//			// We are assuming that we already know the command code so we can ignore it
//			FinsCommandCode commandCode = FinsCommandCode.valueOf(buf.getShort()).get();
//			if (commandCode != FinsCommandCode.MEMORY_AREA_WRITE) {
//				// Throw an error because we are trying to parse a payload of
//				// the incorrect command type.
//			}
//			
//			int addressValue = buf.getInt();
//			FinsIoAddress address = FinsIoAddress.parseFrom(addressValue);
//			short numberOfItems = buf.getShort();
//
//			// Word data
//			List<Short> items = new ArrayList<>();
//
//			for (int i = 0; i < numberOfItems; i++) {
//				short val = buf.getShort();
//				items.add(val);
//			}
//
//			return new FinsMemoryAreaWriteBitCommand(address, items);
//		}
//	}
	
}
