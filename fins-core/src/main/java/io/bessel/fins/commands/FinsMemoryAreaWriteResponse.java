package io.bessel.fins.commands;

import java.nio.ByteBuffer;

import io.bessel.fins.FinsCommandCode;
import io.bessel.fins.FinsEndCode;

public class FinsMemoryAreaWriteResponse extends SimpleFinsResponse {

	public FinsMemoryAreaWriteResponse(final FinsEndCode endCode) {
		super(FinsCommandCode.MEMORY_AREA_WRITE, endCode);
	}
	
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.putShort(this.getCommandCode().getValue());
		buf.putShort(this.getEndCode().getValue());

		return buf.array().clone();
	}
	
	public static FinsMemoryAreaWriteResponse parseFrom(final byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		FinsCommandCode commandCode = FinsCommandCode.valueOf(buf.getShort()).get();
		FinsEndCode endCode = FinsEndCode.valueOf(buf.getShort()).get();
		
		if (commandCode != FinsCommandCode.MEMORY_AREA_WRITE) {
			// We don't have the correct command code for the response type
		}
		
		return new FinsMemoryAreaWriteResponse(endCode);
	}
	
}
