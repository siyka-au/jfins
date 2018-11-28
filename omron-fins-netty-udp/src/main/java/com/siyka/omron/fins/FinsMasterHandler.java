package com.siyka.omron.fins;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.responses.FinsResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class FinsMasterHandler extends SimpleChannelInboundHandler<FinsResponse> {

	final static Logger logger = LoggerFactory.getLogger(FinsMasterHandler.class);

	private final Map<Byte, CompletableFuture<FinsResponse>> futures;

	public FinsMasterHandler(final Map<Byte, CompletableFuture<FinsResponse>> futures) {
		this.futures = futures;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext context, FinsResponse response) throws Exception {
		
		byte serviceAddress = context.channel().attr(ServiceAddressCorrelation.SERVICE_ADDRESS).get();
		
		Optional.ofNullable(this.futures.get(serviceAddress)).ifPresent(future -> future.complete(response));
		ReferenceCountUtil.release(response);
	}

}
