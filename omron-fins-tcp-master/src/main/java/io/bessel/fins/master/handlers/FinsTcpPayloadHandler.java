package io.bessel.fins.master.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bessel.fins.tcp.FinsTcpCommandCode;
import io.bessel.fins.tcp.FinsTcpErrorCode;
import io.bessel.fins.tcp.FinsTcpFrame;
import io.bessel.fins.tcp.FinsTcpFrameBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class FinsTcpPayloadHandler extends ChannelOutboundHandlerAdapter {

	final static Logger logger = LoggerFactory.getLogger(FinsTcpPayloadHandler.class);
	
	@Override
	public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
		if (message instanceof ByteBuf) {
			logger.debug("Stuffing FinsFrame bytes into FINS/TCP payload");
			
			ByteBuf buf = (ByteBuf) message;
			byte[] data = new byte[buf.readableBytes()];
			buf.readBytes(data);
			
			FinsTcpFrame finsTcpFrame = new FinsTcpFrameBuilder()
					.setCommandCode(FinsTcpCommandCode.FINS_FRAME_SEND)
					.setErrorCode(FinsTcpErrorCode.NORMAL)
					.setData(data)
					.build();
			
			context.write(finsTcpFrame, promise);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
		super.exceptionCaught(context, cause);
		logger.error(cause.getLocalizedMessage(), cause);
	}
	
}
