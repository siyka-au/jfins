package com.siyka.omron.fins.codec;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.FinsPdu;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

public class FinsFrameUdpCodec<U extends FinsPdu, V extends FinsPdu> extends MessageToMessageCodec<DatagramPacket, FinsFrame<? extends FinsPdu>> {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final FinsFrameEncoder<U> encoder;
	private final FinsFrameDecoder<V> decoder;
    
    private final Cache<FinsNodeAddress, InetSocketAddress> clients;
    
	public FinsFrameUdpCodec(final FinsFrameEncoder<U> encoder, final FinsFrameDecoder<V> decoder) {
		super();
		this.encoder = encoder;
		this.decoder = decoder;
		
		this.clients = CacheBuilder.newBuilder()
		       .expireAfterAccess(1, TimeUnit.MINUTES)
		       .build();
	}
	
	@Override
	protected void encode(ChannelHandlerContext context, FinsFrame<? extends FinsPdu> frame, List<Object> out) {
		logger.debug("Encoding FINS frame");
		try {
			@SuppressWarnings("unchecked")
			final ByteBuf buffer = this.encoder.encode((FinsFrame<U>) frame);
			final InetSocketAddress remoteAddress = Optional.ofNullable(this.clients.getIfPresent(frame.getHeader().getDestinationAddress())).orElse((InetSocketAddress) context.channel().remoteAddress());
			final InetSocketAddress localAddress = Optional.ofNullable(this.clients.getIfPresent(frame.getHeader().getSourceAddress())).orElse((InetSocketAddress)context.channel().localAddress());
			DatagramPacket packet = new DatagramPacket(buffer, remoteAddress, localAddress);
			out.add(packet);
		} finally {
			ReferenceCountUtil.release(frame);
		}
	}
	
	@Override
	protected void decode(ChannelHandlerContext context, DatagramPacket packet, List<Object> out) {
		logger.debug("Decoding FINS frame");
		final FinsFrame<V> frame = this.decoder.decode(packet.content());
		this.clients.put(frame.getHeader().getSourceAddress(), packet.sender());
		this.clients.put(frame.getHeader().getDestinationAddress(), packet.recipient());
		out.add(frame);
	}
	
}
