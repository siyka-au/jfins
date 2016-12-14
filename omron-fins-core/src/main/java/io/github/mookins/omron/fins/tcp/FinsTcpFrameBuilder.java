package io.github.mookins.omron.fins.tcp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class FinsTcpFrameBuilder {

	private FinsTcpCommandCode commandCode;
	private FinsTcpErrorCode errorCode;
	private byte[] data;

	public FinsTcpFrameBuilder setCommandCode(FinsTcpCommandCode commandCode) {
		this.commandCode = commandCode;
		return this;
	}

	public FinsTcpFrameBuilder setErrorCode(FinsTcpErrorCode errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public FinsTcpFrameBuilder setData(byte[] data) {
		this.data = data;
		return this;
	}

	public FinsTcpFrame build() {
		int length = this.data.length + 8;
		return new FinsTcpFrame(length, this.commandCode, Optional.ofNullable(this.errorCode).orElse(FinsTcpErrorCode.NORMAL), this.data);
	}
	
	public static FinsTcpFrame parseFrom(byte[] frameBytes) throws FinsTcpFrameException, FinsTcpFrameBuilderException {

		ByteBuffer buf = ByteBuffer.wrap(frameBytes);

		// Make sure the frame starts with the four ASCII characters 'FINS'
		byte[] headerBytes = new byte[4];
		buf.get(headerBytes);
		String header = new String(headerBytes, StandardCharsets.US_ASCII);
		if (!header.equals("FINS"))
			throw new FinsTcpFrameException("FINS/TCP header != \"FINS\"");

		// Read in the rest of the header data, 12 bytes in all
		int length = buf.getInt();
		int commandCodeInt = buf.getInt();
		FinsTcpCommandCode commandCode = FinsTcpCommandCode.valueOf(commandCodeInt).orElseThrow(() -> new FinsTcpFrameBuilderException(String.format("No command code found for ordinal 0x%04x", commandCodeInt)));
		int errorCodeInt = buf.getInt();
		FinsTcpErrorCode errorCode = FinsTcpErrorCode.valueOf(errorCodeInt).orElseThrow(() -> new FinsTcpFrameBuilderException(String.format("No error code found for ordinal 0x%04x", errorCodeInt)));

		// The header contains the length, so read in this many bytes minus the
		// 8 we already read in (commandCode and errorCode)
		byte[] data = new byte[length - 8];
		buf.get(data);

		FinsTcpFrame finsTcpFrame = new FinsTcpFrameBuilder()
			.setCommandCode(commandCode)
			.setErrorCode(errorCode)
			.setData(data)
			.build();

		return finsTcpFrame;
	}
	
}