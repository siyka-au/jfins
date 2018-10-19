package com.siyka.omron.fins.master;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.NodeAddress;
import com.siyka.omron.fins.FinsPdu;
import com.siyka.omron.fins.codec.FinsCommandFrameEncoder;
import com.siyka.omron.fins.codec.FinsFrameDecoder;
import com.siyka.omron.fins.codec.FinsFrameEncoder;
import com.siyka.omron.fins.codec.FinsResponseFrameDecoder;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.responses.FinsResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

public class FinsFrameMasterUdpCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame<?>> {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	   
    private final Cache<Byte, FinsCommand<?>> outgoingCommands;
    
	public FinsFrameMasterUdpCodec() {
		super();
		this.encoder = new FinsCommandFrameEncoder();
		this.decoder = new FinsResponseFrameDecoder();
		
		this.outgoingCommands = CacheBuilder.newBuilder()
			       .expireAfterAccess(1, TimeUnit.MINUTES)
			       .build();
	}
	
	@Override
	protected void encode(ChannelHandlerContext context, FinsFrame frame, List<Object> out) {
		logger.debug("Encoding FINS frame");
		try {
			@SuppressWarnings("unchecked")
			final ByteBuf buffer = this.encoder.encode(frame);
			
			this.outgoingCommands.put(frame.getHeader().getServiceAddress(), (FinsCommand) frame.getPdu());
			
			final InetSocketAddress remoteAddress = (InetSocketAddress) context.channel().remoteAddress();
			final InetSocketAddress localAddress = (InetSocketAddress) context.channel().localAddress();
			
			DatagramPacket packet = new DatagramPacket(buffer, remoteAddress, localAddress);
			
			out.add(packet);
		} finally {
			ReferenceCountUtil.release(frame);
		}
	}
	
	@Override
	protected void decode(ChannelHandlerContext context, DatagramPacket packet, List<Object> out) {
		logger.debug("Decoding FINS frame");
		final FinsFrame<FinsReponse> frame = this.decoder.decode(packet.content());
		out.add(frame);
	}
	
}
