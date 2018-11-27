package com.siyka.omron.fins.master;

import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsPdu;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

public class FinsResponseDecoder implements FinsPduDecoder {

	@Override
	public FinsPdu decode(ByteBuf buffer) throws DecoderException {
		final FinsHeader header = Codecs.decodeHeader(buffer);
		final CommandCode commandCode = Codecs.decodeCommandCode(buffer);
		
		switch(header.getMessageType()) {
			case RESPONSE: {
				out.add(decodeResponse(context, header, commandCode, buffer));
				break;
			}
			
			case COMMAND: {
				out.add(decodeCommand(context, header, commandCode, buffer));
				break;
			}
		}
	}

}
