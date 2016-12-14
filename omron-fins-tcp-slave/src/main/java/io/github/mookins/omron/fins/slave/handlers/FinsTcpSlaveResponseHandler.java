package io.github.mookins.omron.fins.slave.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mookins.omron.fins.tcp.FinsTcpCommandCode;
import io.github.mookins.omron.fins.tcp.FinsTcpErrorCode;
import io.github.mookins.omron.fins.tcp.FinsTcpFrame;
import io.github.mookins.omron.fins.tcp.FinsTcpFrameBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class FinsTcpSlaveResponseHandler extends ChannelOutboundHandlerAdapter {

	final static Logger logger = LoggerFactory.getLogger(FinsTcpSlaveResponseHandler.class);

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof ByteBuf) {
			logger.debug("Got a ByteBuf, going to pack it into a FINS/TCP frame");
			ByteBuf buf = (ByteBuf) msg;
			byte[] data = new byte[buf.readableBytes()];
			buf.readBytes(data);
			FinsTcpFrame finsTcpFrame = new FinsTcpFrameBuilder()
					.setCommandCode(FinsTcpCommandCode.FINS_FRAME_SEND)
					.setErrorCode(FinsTcpErrorCode.NORMAL)
					.setData(data)
					.build();
			logger.debug(finsTcpFrame.toString());
			ctx.writeAndFlush(finsTcpFrame, promise);
		}
	}

}
