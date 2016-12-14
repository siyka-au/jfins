package io.bessel.fins.codec;

import java.net.InetSocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bessel.fins.FinsFrame;
import io.bessel.fins.FinsFrameBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

public class FinsFrameUdpCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame> {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void encode(ChannelHandlerContext ctx, FinsFrame frame, List<Object> out) throws Exception {
		try {
			ByteBuf buf = Unpooled.wrappedBuffer(frame.toByteArray());
			DatagramPacket packet = new DatagramPacket(buf, new InetSocketAddress("192.168.250.10", 9600));
			out.add(packet);
		} finally {
			ReferenceCountUtil.release(frame);
		}
	}

	@Override
	protected void decode(ChannelHandlerContext context, DatagramPacket packet, List<Object> out) throws Exception {
		byte[] data = new byte[packet.content()
			.readableBytes()];
		packet.content()
			.readBytes(data);
		FinsFrame frame = FinsFrameBuilder.parseFrom(data);
		out.add(frame);
	}
	
}
