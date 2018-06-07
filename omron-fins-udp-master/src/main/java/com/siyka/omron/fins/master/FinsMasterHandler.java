package com.siyka.omron.fins.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsSimpleFrame;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class FinsMasterHandler extends SimpleChannelInboundHandler<FinsSimpleFrame> {

	final static Logger logger = LoggerFactory.getLogger(FinsMasterHandler.class);

	private final FinsNettyUdpMaster master;

	public FinsMasterHandler(final FinsNettyUdpMaster master) {
		this.master = master;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FinsFrame frame) throws Exception {
		this.master.getSendFuture().complete(frame);
		ReferenceCountUtil.release(frame);
	}

}
