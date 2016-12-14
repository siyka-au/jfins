package io.bessel.fins.slave;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bessel.fins.FinsSlave;
import io.bessel.fins.FinsSlaveException;
import io.bessel.fins.MemoryAreaWriteCommandHandler;
import io.bessel.fins.codec.FinsFrameUdpCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FinsNettyUdpSlave implements FinsSlave {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Integer port;
	private Optional<MemoryAreaWriteCommandHandler> memoryAreaWriteCommandHandler;
	
	private EventLoopGroup group;

	public FinsNettyUdpSlave(Integer listenPort) {
		super();
		this.port = listenPort;
		this.memoryAreaWriteCommandHandler = Optional.empty();
	}

	@Override
	public void start() throws FinsSlaveException {
		logger.debug("Starting server");
		
        this.group = new NioEventLoopGroup();
        try {
        	Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(this.group)
            	.channel(NioDatagramChannel.class)
            	.option(ChannelOption.SO_BROADCAST, true)
            	.handler(new ChannelInitializer<NioDatagramChannel>() {
            		@Override
            		public void initChannel(NioDatagramChannel channel) throws Exception {
            			channel.pipeline()
							.addLast(new LoggingHandler(LogLevel.DEBUG))
							// Datagram 
							.addLast(new FinsFrameUdpCodec())
							// FINS
							.addLast(new FinsSlaveCommandHandler(FinsNettyUdpSlave.this));
            		}
				});

            bootstrap.bind("0.0.0.0", this.port).sync();
        } catch (InterruptedException exception) {
			throw new FinsSlaveException(exception);
		}
	}
	
	@Override
	public void shutdown() {
		logger.debug("Shutting down");
		this.group.shutdownGracefully();
	}

	@Override
	public void setMemoryAreaWriteHandler(MemoryAreaWriteCommandHandler handler) {
		this.memoryAreaWriteCommandHandler = Optional.ofNullable(handler);
	}

	@Override
	public Optional<MemoryAreaWriteCommandHandler> getMemoryAreaWriteHandler() {
		return this.memoryAreaWriteCommandHandler;
	}

	@Override
	public void close() throws Exception {
		this.shutdown();
	}
	
}
