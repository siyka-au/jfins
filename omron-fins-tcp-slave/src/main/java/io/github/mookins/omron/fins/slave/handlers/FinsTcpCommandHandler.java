package io.github.mookins.omron.fins.slave.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mookins.omron.fins.tcp.FinsTcpCommandCode;
import io.github.mookins.omron.fins.tcp.FinsTcpErrorCode;
import io.github.mookins.omron.fins.tcp.FinsTcpFrame;
import io.github.mookins.omron.fins.tcp.FinsTcpFrameBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class FinsTcpCommandHandler extends SimpleChannelInboundHandler<FinsTcpFrame> {

	final static Logger logger = LoggerFactory.getLogger(FinsTcpCommandHandler.class);

	protected void channelRead0(ChannelHandlerContext context, FinsTcpFrame finsTcpFrame) throws Exception {
		logger.debug(String.format("FINS/TCP command handler = %s, ", finsTcpFrame.getCommandCode().toString()));

		switch (finsTcpFrame.getCommandCode()) {
		case FINS_SERVER_NODE_ADDRESS_DATA_SEND:
			break;

		case FINS_CLIENT_NODE_ADDRESS_DATA_SEND:
			// Client node address sent, send ours back
			context.writeAndFlush(this.handleFinsClientNodeAddressDataSend(finsTcpFrame));
			break;

		case FINS_FRAME_SEND:
			// Extract the payload which should always be a FINS frame anyway, and kick it down the pipeline
			// We don't parse the FINS frame here so that we can reuse the FINS byte parser which can be
			// used for UDP transport
			logger.debug("Extracting payload and kicking it down the pipeline");
			context.fireChannelRead(Unpooled.wrappedBuffer(finsTcpFrame.getData()));
			break;
			
		case CONNECTION_CONFIRMATION:
			// Destroy the frames. This is purely to initiate a TCP send so that
			// a TCP ACK can be sent back to ensure that the other end is still
			// alive. See Omron W421 page 180
			logger.debug("Connection confirmed, TCP ACK will be sent by underlying stack");
			
		case FINS_FRAME_SEND_ERROR_NOTIFCATION:
			// TODO implement proper logic
			logger.debug("Closing connection");
			context.close();
			break;
			
		default:
			// Unknown FINS/TCP command so close down the channel
			logger.error(String.format("Closing channel(%s), unknown FINS/TCP command received", context.channel().remoteAddress().toString()));
			context.close();
			break;
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
