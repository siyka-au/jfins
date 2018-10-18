package com.siyka.omron.fins.codec;

import java.net.InetSocketAddress;
import java.util.List;
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
import io.netty.util.ReferenceCountUtil;

public class FinsFrameUdpCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame> {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final FinsCommandFrameDecoder decoder;
    private final FinsResponseFrameEncoder encoder;
    private final Cache<FinsNodeAddress, InetSocketAddress> clients;
    
	public FinsFrameUdpCodec(FinsCommandFrameDecoder decoder, FinsResponseFrameEncoder encoder) {
		super();
		this.decoder = decoder;
		this.encoder = encoder;
		this.clients = CacheBuilder.newBuilder()
		       .expireAfterAccess(1, TimeUnit.MINUTES)
		       .build();
	}

	@Override
	protected void decode(ChannelHandlerContext context, DatagramPacket packet, List<Object> out) {
		final FinsFrame frame = this.decoder.decode(packet.content());
		this.clients.put(frame.getHeader().getSourceAddress(), packet.sender());
		this.clients.put(frame.getHeader().getDestinationAddress(), packet.recipient());
		out.add(frame);
	}
	
	@Override
	protected void encode(ChannelHandlerContext context, FinsFrame frame, List<Object> out) {
		try {
			ByteBuf buf = this.encoder.encode(frame);
			final InetSocketAddress recipient = this.clients.getIfPresent(frame.getHeader().getDestinationAddress());
			final InetSocketAddress sender = this.clients.getIfPresent(frame.getHeader().getSourceAddress());
			DatagramPacket packet = new DatagramPacket(buf, recipient,sender);
			out.add(packet);
		} finally {
			ReferenceCountUtil.release(frame);
		}
	}
}
