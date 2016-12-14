package io.bessel.fins.tcp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import io.bessel.fins.util.StringUtilities;

public class FinsTcpFrame {

	private final String header = "FINS";
	private final Integer length;
	private final FinsTcpCommandCode commandCode;
	private final FinsTcpErrorCode errorCode;
	private final byte[] data;

	public FinsTcpFrame(final Integer length, final FinsTcpCommandCode commandCode, final FinsTcpErrorCode errorCode,
			final byte[] data) {
		this.length = length;
		this.commandCode = commandCode;
		this.errorCode = errorCode;
		this.data = data;
	}

	public String getHeader() {
		return this.header;
	}
	
	public Integer getLength() {
		return this.length;
	}

	public Integer getDataLength() {
		return this.getLength() - 8;
	}

	public FinsTcpCommandCode getCommandCode() {
		return this.commandCode;
	}

	public FinsTcpErrorCode getErrorCode() {
		return this.errorCode;
	}

	public byte[] getData() {
		return this.data;
	}

	public byte[] toByteArray() {
		int frameLength = 16 + this.getData().length;
		byte[] data = new byte[frameLength];
		
		ByteBuffer buf = ByteBuffer.allocate(frameLength);
		buf.put(this.getHeader().getBytes(StandardCharsets.US_ASCII))
			.putInt(this.getLength())
			.putInt(this.getCommandCode().getValue())
			.putInt(this.getErrorCode().getValue())
			.put(this.getData())
			.flip();
		buf.get(data);
		
		return data;
	}
	
	public String toString() {
		return String.format("FINS/TCP length[%d] cmd[%s] err[%s] data[%s]", this.length, this.commandCode,
				this.errorCode, StringUtilities.getHexString(this.data));
	}
	
}
