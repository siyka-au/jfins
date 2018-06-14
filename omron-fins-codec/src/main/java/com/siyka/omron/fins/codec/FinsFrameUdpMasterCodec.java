package com.siyka.omron.fins.codec;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

public class FinsFrameUdpMasterCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame> {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final InetSocketAddress destinationSocketAddress;
	private final InetSocketAddress sourceSocketAddress;

	private final FinsFrameEncoder encoder;
	private final FinsFrameDecoder decoder;
		
	public FinsFrameUdpMasterCodec(final FinsFrameEncoder encoder, final FinsFrameDecoder decoder, final InetSocketAddress destinationSocketAddress, final InetSocketAddress sourceSocketAddress) {
		super();
		Objects.requireNonNull(encoder);
		Objects.requireNonNull(decoder);
		Objects.requireNonNull(destinationSocketAddress);
		Objects.requireNonNull(sourceSocketAddress);
		this.encoder = encoder;
		this.decoder = decoder;
		this.destinationSocketAddress = destinationSocketAddress;
		this.sourceSocketAddress = sourceSocketAddress;
	}
	
	@Override
	protected void encode(final ChannelHandlerContext context, final FinsFrame frame, final List<Object> out) {
		final ByteBuf buffer = this.encoder.encode(frame);
		DatagramPacket packet = new DatagramPacket(buffer, this.destinationSocketAddress, this.sourceSocketAddress);
		out.add(packet);
	}
	
	@Override
	protected void decode(final ChannelHandlerContext context, final DatagramPacket packet, final List<Object> out) {
		final FinsFrame frame = this.decoder.decode(packet.content());
		out.add(frame);
	}

}
