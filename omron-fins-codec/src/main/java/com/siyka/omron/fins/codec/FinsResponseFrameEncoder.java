package com.siyka.omron.fins.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.responses.FinsResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

public class FinsResponseFrameEncoder implements FinsFrameEncoder {
	
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public ByteBuf encode(final FinsFrame response) {
		try {
			ByteBuf buffer = Unpooled.buffer();
			byte icf = 0x00;
			if (response.getHeader().useGateway() == true) icf = (byte) (icf | 0x80);
			if (response.getHeader().getMessageType() == FinsHeader.MessageType.RESPONSE) icf = (byte) (icf | 0x40);
			if (response.getHeader().getResponseAction() == FinsHeader.ResponseAction.RESPONSE_NOT_REQUIRED) icf = (byte) (icf | 0x01);
			buffer.writeByte(icf);
			buffer.writeByte(0x00);
			buffer.writeByte(response.getHeader().getGatewayCount());
			buffer.writeByte(response.getHeader().getDestinationAddress().getNetwork());
			buffer.writeByte(response.getHeader().getDestinationAddress().getNode());
			buffer.writeByte(response.getHeader().getDestinationAddress().getUnit());
			buffer.writeByte(response.getHeader().getSourceAddress().getNetwork());
			buffer.writeByte(response.getHeader().getSourceAddress().getNode());
			buffer.writeByte(response.getHeader().getSourceAddress().getUnit());
			buffer.writeByte(response.getHeader().getServiceAddress());
			buffer.writeShort(response.getPdu().getCommandCode().getValue());
			
			if (response instanceof FinsResponse) {
				buffer.writeShort(((FinsResponse) response).getEndCode().getValue());
			}
			return buffer;
		} finally {
			ReferenceCountUtil.release(response);
		}
	}
	
}
