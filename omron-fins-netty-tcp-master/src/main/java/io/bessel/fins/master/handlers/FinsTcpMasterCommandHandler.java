package io.bessel.fins.master.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bessel.fins.tcp.FinsTcpCommandCode;
import io.bessel.fins.tcp.FinsTcpErrorCode;
import io.bessel.fins.tcp.FinsTcpFrame;
import io.bessel.fins.tcp.FinsTcpFrameBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class FinsTcpMasterCommandHandler extends SimpleChannelInboundHandler<FinsTcpFrame> {

	final static Logger logger = LoggerFactory.getLogger(FinsTcpMasterCommandHandler.class);

	protected void channelRead0(ChannelHandlerContext context, FinsTcpFrame finsTcpFrame) throws Exception {
		FinsTcpFrame outFrame = null;

		logger.debug(String.format("FINS/TCP command handler = %s, ", finsTcpFrame.getCommandCode().toString()));

		switch (finsTcpFrame.getCommandCode()) {
		case FINS_CLIENT_NODE_ADDRESS_DATA_SEND:
			// Client node address sent, send ours back
			outFrame = this.handleFinsClientNodeAddressDataSend(finsTcpFrame);
			break;

		case FINS_FRAME_SEND:
			// Extract the payload which should always be a FINS frame anyway, and kick it down the pipline
			// We don't parse the FINS frame here so that we can reuse the FINS byte parser which can be
			// used for UDP transport
			logger.debug("Extracting payload and kicking it down the pipeline");
			
			context.fireChannelRead(Unpooled.wrappedBuffer(finsTcpFrame.getData()));
			break;

		case FINS_FRAME_SEND_ERROR_NOTIFCATION:
			logger.debug("Closing connection");
			context.close();
			break;

		case FINS_SERVER_NODE_ADDRESS_DATA_SEND:
		case CONNECTION_CONFIRMATION:
		default:
			logger.debug("Don't know what's going on, closing connection");
			context.close();
			break;
		}

		if (outFrame != null) {
			logger.debug("Writing and flushing FINS/TCP frame");
			context.writeAndFlush(outFrame);
		}
	}

	protected FinsTcpFrame handleFinsClientNodeAddressDataSend(FinsTcpFrame frame) throws Exception {
		logger.debug("Performing connection address exchange");
		final int dataSize = 8;
		ByteBuf buf = Unpooled.buffer(dataSize);
		buf.writeBytes(frame.getData());
		buf.writeInt(1);
		byte[] data = new byte[dataSize];
		buf.readBytes(data);
		ReferenceCountUtil.release(buf);

		FinsTcpFrame finsTcpFrame = new FinsTcpFrameBuilder()
				.setCommandCode(FinsTcpCommandCode.FINS_SERVER_NODE_ADDRESS_DATA_SEND)
				.setErrorCode(FinsTcpErrorCode.NORMAL)
				.setData(data)
				.build();
		
		return finsTcpFrame;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
		// Close the connection when an exception is raised.
		logger.error("FINS TCP command handler error", cause);
		context.close();
	}
}
