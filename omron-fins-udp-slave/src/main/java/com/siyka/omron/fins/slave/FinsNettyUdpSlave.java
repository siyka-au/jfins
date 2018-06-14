package com.siyka.omron.fins.slave;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.codec.FinsCommandFrameDecoder;
import com.siyka.omron.fins.codec.FinsFrameUdpSlaveCodec;
import com.siyka.omron.fins.codec.FinsResponseFrameEncoder;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.responses.FinsResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FinsNettyUdpSlave implements FinsSlave {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final FinsNettyUdpSlaveConfig config;
	
	private final AtomicReference<ServiceCommandHandler> commandHandler = new AtomicReference<>(new ServiceCommandHandler() {});

	public FinsNettyUdpSlave(final FinsNettyUdpSlaveConfig config) {
		Objects.requireNonNull(config);
		this.config = config;
	}
	
	public FinsNettyUdpSlaveConfig getConfig() {
		return this.config;
	}
	
	@Override
	public CompletableFuture<FinsSlave> bind() {
		CompletableFuture<FinsSlave> bindFuture = new CompletableFuture<>();
		new Bootstrap().group(this.config.getGroup())
			.channel(NioDatagramChannel.class)
			.option(ChannelOption.SO_BROADCAST, true)
			.handler(new ChannelInitializer<NioDatagramChannel>() {
				@Override
				public void initChannel(NioDatagramChannel channel) throws Exception {
					channel.pipeline()
						// Datagram 
						.addLast(new FinsFrameUdpSlaveCodec(new FinsResponseFrameEncoder(), new FinsCommandFrameDecoder()))
						.addLast(new LoggingHandler(LogLevel.DEBUG))
						// FINS
						.addLast(new FinsSlaveCommandHandler(FinsNettyUdpSlave.this))
						;
				}
			})
			.bind(this.getConfig().getSocketAddress().getAddress(), this.getConfig().getSocketAddress().getPort())
			.addListener((ChannelFuture future) -> {
				if (future.isSuccess()) {
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
		this.config.getGroup().shutdownGracefully();
	}

	@Override
	public void setHandler(final ServiceCommandHandler handler) {
		this.commandHandler.set(handler);
	}
	
	private static class FinsNettyUdpSlaveServiceCommand<Command extends FinsCommand, Response extends FinsResponse> implements ServiceCommand<Command, Response> {
		
		private final ChannelHandlerContext context;
		private final FinsHeader header;
		private final Command command;
		
		private FinsNettyUdpSlaveServiceCommand(ChannelHandlerContext context, final FinsHeader header, Command command) {
			this.context = context;
			this.header = FinsHeader.builder()
					.messageType(FinsHeader.MessageType.RESPONSE)
					.destinationAddress(header.getSourceAddress())
					.sourceAddress(header.getDestinationAddress())
					.serviceAddress(header.getServiceAddress())
					.build();
			this.command = command;
		}

		@Override
		public Command getCommand() {
			return this.command;
		}

		@Override
		public void sendResponse(Response response) {
			this.context.writeAndFlush(new FinsFrame(this.header, response));
		}
		
		@SuppressWarnings("unchecked")
		public static <Command extends FinsCommand, Response extends FinsResponse> FinsNettyUdpSlaveServiceCommand<Command, Response> of(final FinsFrame frame, final ChannelHandlerContext context) {
			return new FinsNettyUdpSlaveServiceCommand<>(context, frame.getHeader(), (Command) frame.getPdu());
		}
		
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
		logger.debug("Exception has been caught", cause);
	}
	
	private static class FinsSlaveCommandHandler extends SimpleChannelInboundHandler<FinsFrame> {

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
