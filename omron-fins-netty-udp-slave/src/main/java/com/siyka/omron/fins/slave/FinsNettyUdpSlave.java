package com.siyka.omron.fins.slave;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.codec.FinsCommandFrameDecoder;
import com.siyka.omron.fins.codec.FinsFrameUdpCodec;
import com.siyka.omron.fins.codec.FinsResponseFrameEncoder;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.responses.FinsResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FinsNettyUdpSlave implements FinsSlave {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final AtomicReference<ServiceCommandHandler> commandHandler = new AtomicReference<>(new ServiceCommandHandler() {});
	
	private final EventLoopGroup group;

	public FinsNettyUdpSlave() {
		this(new NioEventLoopGroup());
	}
	
	public FinsNettyUdpSlave(final EventLoopGroup group) {
		Objects.requireNonNull(group);
		this.group = group;
	}
	
	@Override
	public CompletableFuture<FinsSlave> bind(final String host, final int port) {
		CompletableFuture<FinsSlave> bindFuture = new CompletableFuture<>();
    	Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(this.group)
        	.channel(NioDatagramChannel.class)
        	.option(ChannelOption.SO_BROADCAST, true)
        	.handler(new ChannelInitializer<NioDatagramChannel>() {
        		@Override
        		public void initChannel(NioDatagramChannel channel) throws Exception {
        			channel.pipeline()
//						.addLast(new LoggingHandler(LogLevel.DEBUG))
						// Datagram 
						.addLast(new FinsFrameUdpCodec(new FinsCommandFrameDecoder(), new FinsResponseFrameEncoder()))
						.addLast(new LoggingHandler(LogLevel.DEBUG))
						// FINS
						.addLast(new FinsSlaveCommandHandler(FinsNettyUdpSlave.this))
						;
        		}
			});
        
        bootstrap.bind(host, port).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
//                Channel channel = future.channel();
//                serverChannels.put(channel.localAddress(), channel);
                bindFuture.complete(FinsNettyUdpSlave.this);
            } else {
                bindFuture.completeExceptionally(future.cause());
            }
        });
        
        return bindFuture;
	}
	
	@Override
	public void shutdown() {
		logger.debug("Shutting down");
		this.group.shutdownGracefully();
	}

	@Override
	public void setHandler(final ServiceCommandHandler handler) {
		this.commandHandler.set(handler);
	}
	
	public void onChannelRead(final ChannelHandlerContext context, final FinsFrame frame) throws Exception {
		final ServiceCommandHandler handler = this.commandHandler.get();
        if (handler == null) return;
        
        switch (frame.getPdu().getCommandCode()) {
        	case MEMORY_AREA_WRITE:
        		handler.onMemoryAreaWrite(FinsNettyUdpSlaveServiceCommand.of(frame, context));
        		break;
        	
        	default:
        }
	}
	
	public void onChannelInactive(final ChannelHandlerContext context) {
		// TODO Auto-generated method stub
		logger.info("Channel has gone inactive");
	}

	public void onExceptionCaught(final ChannelHandlerContext context, final Throwable cause) {
		// TODO Auto-generated method stub
		logger.error("Cracked the wobblies", cause);
	}
	
	public static class FinsSlaveCommandHandler extends SimpleChannelInboundHandler<FinsFrame> {

		private final FinsNettyUdpSlave slave;

		public FinsSlaveCommandHandler(final FinsNettyUdpSlave slave) {
			this.slave = slave;
		}

        @Override
        protected void channelRead0(final ChannelHandlerContext context, final FinsFrame frame) throws Exception {
            this.slave.onChannelRead(context, frame);
        }

        @Override
        public void channelInactive(final ChannelHandlerContext context) throws Exception {
        	this.slave.onChannelInactive(context);
        }

        @Override
        public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) throws Exception {
        	this.slave.onExceptionCaught(context, cause);
        }
        
	}
	

}
