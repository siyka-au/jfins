package io.github.mookins.omron.fins.commands;

import java.nio.ByteBuffer;

import io.github.mookins.omron.fins.FinsCommandCode;
import io.github.mookins.omron.fins.FinsIoAddress;

public final class FinsMemoryAreaReadCommand extends SimpleAddressableFinsCommand {

	private final short itemCount;

	public FinsMemoryAreaReadCommand(final FinsIoAddress address, final short itemCount) {
		super(FinsCommandCode.MEMORY_AREA_READ, address);
		this.itemCount = itemCount;
	}
	
	public FinsMemoryAreaReadCommand(final FinsIoAddress address, final int itemCount) {
		this(address, (short) itemCount);
	}

	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putShort(this.getCommandCode().getValue());
		buf.put(this.getIoAddress().getMemoryArea().getValue());
		buf.putShort(this.getIoAddress().getAddress());
		buf.put(this.getIoAddress().getBitOffset());
		buf.putShort(itemCount);
		
		buf.flip();
		byte[] bytes = new byte[buf.remaining()];
		buf.get(bytes);

		return bytes;
	}

}
