package io.bessel.fins.master.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bessel.fins.FinsFrame;
import io.bessel.fins.master.FinsNettyTcpMaster;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FinsMasterHandler extends ChannelInboundHandlerAdapter {

	final static Logger logger = LoggerFactory.getLogger(FinsMasterHandler.class);
	
	private final FinsNettyTcpMaster master;

	public FinsMasterHandler(final FinsNettyTcpMaster master) {
		this.master = master;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
		if (message instanceof FinsFrame) {
			this.master.getQueue().remove().complete((FinsFrame) message);
		}
	}	
	
}
