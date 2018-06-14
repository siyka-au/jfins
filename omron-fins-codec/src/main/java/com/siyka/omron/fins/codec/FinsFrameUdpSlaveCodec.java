package com.siyka.omron.fins.codec;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsNodeAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

public class FinsFrameUdpSlaveCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame> {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    private final FinsFrameEncoder encoder;
    private final FinsFrameDecoder decoder;

    private final Cache<FinsNodeAddress, InetSocketAddress> addresses;
    
	public FinsFrameUdpSlaveCodec(final FinsFrameEncoder encoder, final FinsFrameDecoder decoder) {
		super();
		Objects.requireNonNull(encoder);
		Objects.requireNonNull(decoder);
		this.encoder = encoder;
		this.decoder = decoder;
		this.addresses = CacheBuilder.newBuilder()
				.expireAfterAccess(1, TimeUnit.MINUTES)
				.build();
	}
	
	@Override
	protected void encode(final ChannelHandlerContext context, final FinsFrame frame, final List<Object> out) {
		final ByteBuf buffer = this.encoder.encode(frame);
		final InetSocketAddress recipient = this.addresses.getIfPresent(frame.getHeader().getDestinationAddress());
		final InetSocketAddress sender = this.addresses.getIfPresent(frame.getHeader().getSourceAddress());
		DatagramPacket packet = new DatagramPacket(buffer, recipient, sender);
		out.add(packet);
	}
	
	@Override
	protected void decode(final ChannelHandlerContext context, final DatagramPacket packet, final List<Object> out) {
		final FinsFrame frame = this.decoder.decode(packet.content());
		this.addresses.put(frame.getHeader().getSourceAddress(), packet.sender());
		this.addresses.put(frame.getHeader().getDestinationAddress(), packet.recipient());
		out.add(frame);
	}
	
}
