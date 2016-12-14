package io.bessel.fins.slave;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bessel.fins.FinsSlave;
import io.bessel.fins.FinsSlaveException;
import io.bessel.fins.MemoryAreaWriteCommandHandler;
import io.bessel.fins.codec.FinsFrameCodec;
import io.bessel.fins.codec.FinsTcpFrameCodec;
import io.bessel.fins.slave.handlers.FinsSlaveCommandHandler;
import io.bessel.fins.slave.handlers.FinsTcpCommandHandler;
import io.bessel.fins.slave.handlers.FinsTcpSlaveResponseHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FinsNettyTcpSlave implements FinsSlave {

	final static Logger logger = LoggerFactory.getLogger(FinsNettyTcpSlave.class);

	private final String host;
	private final Integer port;
	private Optional<MemoryAreaWriteCommandHandler> memoryAreaWriteCommandHandler;
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public FinsNettyTcpSlave(String host, Integer port) {
		super();
		this.host = host;
		this.port = port;
		this.memoryAreaWriteCommandHandler = Optional.empty();
	}

	@Override
	public void start() throws FinsSlaveException {
		logger.info("Starting server");
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(this.bossGroup, this.workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel channel) throws Exception {
						channel.pipeline()
							.addLast(new LoggingHandler(LogLevel.DEBUG))
							// ByteBuf
							.addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4))
							// chunked ByteBuf
							.addLast(new FinsTcpFrameCodec())
							// FINS/TCP
							.addLast(new FinsTcpCommandHandler())
							.addLast(new FinsTcpSlaveResponseHandler())
							// ByteBuf
							.addLast(new FinsFrameCodec())
							// FINS
							.addLast(new FinsSlaveCommandHandler(FinsNettyTcpSlave.this));
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			bootstrap.bind(this.host, this.port).sync();
			logger.info("Listening on {}:{}", this.host, this.port);
		} catch (InterruptedException ex) {
			logger.info("Interrupted", ex);
		} finally {
			
		}
	}
	
	public void shutdown() {
		logger.info("Shutting down");
		this.workerGroup.shutdownGracefully();
		this.bossGroup.shutdownGracefully();
	}

	@Override
	public void setMemoryAreaWriteHandler(MemoryAreaWriteCommandHandler handler) {
		this.memoryAreaWriteCommandHandler = Optional.of(handler);
	}

	@Override
	public Optional<MemoryAreaWriteCommandHandler> getMemoryAreaWriteHandler() {
		return this.memoryAreaWriteCommandHandler;
	}
	
}
