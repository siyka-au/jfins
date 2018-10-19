package com.siyka.omron.fins.codec;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.responses.FinsResponse;

import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

public class FinsResponseFrameEncoder implements FinsFrameEncoder {
	
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public ByteBuffer encode(final FinsFrame response) {
		try {
			final ByteBuffer buffer = ByteBuffer.allocate(14);
			byte icf = 0x00;
			if (response.getHeader().useGateway() == true) icf = (byte) (icf | 0x80);
			if (response.getHeader().getMessageType() == FinsHeader.MessageType.RESPONSE) icf = (byte) (icf | 0x40);
			if (response.getHeader().getResponseAction() == FinsHeader.ResponseAction.RESPONSE_NOT_REQUIRED) icf = (byte) (icf | 0x01);
			buffer.put(icf);
			buffer.put((byte) 0x00);
			buffer.put(response.getHeader().getGatewayCount());
			buffer.put(response.getHeader().getDestinationAddress().getNetwork());
			buffer.put(response.getHeader().getDestinationAddress().getNode());
			buffer.put(response.getHeader().getDestinationAddress().getUnit());
			buffer.put(response.getHeader().getSourceAddress().getNetwork());
			buffer.put(response.getHeader().getSourceAddress().getNode());
			buffer.put(response.getHeader().getSourceAddress().getUnit());
			buffer.put(response.getHeader().getServiceAddress());
			buffer.putShort(response.getPdu().getCommandCode().getValue());
			
			if (response instanceof FinsResponse) {
				buffer.putShort(((FinsResponse) response).getEndCode().getValue());
			}
			return buffer;
		} finally {
			ReferenceCountUtil.release(response);
		}
	}
	
}
