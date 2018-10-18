package com.siyka.omron.fins.master;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class FinsMasterHandler extends SimpleChannelInboundHandler<FinsFrame> {

	final static Logger logger = LoggerFactory.getLogger(FinsMasterHandler.class);

	private final Map<Byte, CompletableFuture<FinsFrame>> futures;

	public FinsMasterHandler(final Map<Byte, CompletableFuture<FinsFrame>> futures) {
		this.futures = futures;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final FinsFrame frame) throws Exception {
		Optional.ofNullable(this.futures.remove(frame.getHeader().getServiceAddress())).ifPresent(f -> f.complete(frame));
		ReferenceCountUtil.release(frame);
	}

}
