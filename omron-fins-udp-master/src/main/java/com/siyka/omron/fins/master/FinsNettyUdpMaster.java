package com.siyka.omron.fins.master;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsHeader.MessageType;
import com.siyka.omron.fins.FinsHeader.ResponseAction;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.Word;
import com.siyka.omron.fins.codec.FinsCommandFrameEncoder;
import com.siyka.omron.fins.codec.FinsFrameUdpMasterCodec;
import com.siyka.omron.fins.codec.FinsMasterStateManager;
import com.siyka.omron.fins.codec.FinsResponseFrameDecoder;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteBitCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteWordCommand;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.responses.FinsSimpleResponse;
import com.siyka.omron.fins.responses.MemoryAreaReadResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.Timeout;

public class FinsNettyUdpMaster implements FinsMaster {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final FinsNettyUdpMasterConfig config;

	private Channel channel;
	private final Map<Byte, PendingCommand<? extends FinsResponse>> pendingCommands = new ConcurrentHashMap<>();
	private final AtomicInteger serviceAddress = new AtomicInteger(0);

	private final FinsHeader.Builder headerBuilder;

	public FinsNettyUdpMaster(final FinsNettyUdpMasterConfig config) {
		Objects.requireNonNull(config);
		this.config = config;
		
		this.headerBuilder = FinsHeader.builder()
				.useGateway(true)
				.destinationAddress(config.getDestinationNodeAddress())
				.sourceAddress(config.getSourceNodeAddress())
				.messageType(MessageType.COMMAND);
	}

	public FinsNettyUdpMasterConfig getConfig() {
		return this.config;
	}

	public CompletableFuture<FinsMaster> connect() {
		final CompletableFuture<FinsMaster> masterFuture = new CompletableFuture<>();
		final FinsMasterStateManager manager = new FinsMasterStateManager();
		new Bootstrap().group(this.config.getEventLoop())
				.channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(new ChannelInitializer<NioDatagramChannel>() {
					@Override
					public void initChannel(NioDatagramChannel channel) throws Exception {
						channel.pipeline()
								// Datagram 
								.addLast(new FinsFrameUdpMasterCodec(
										new FinsCommandFrameEncoder(manager),
										new FinsResponseFrameDecoder(manager),
										config.getDestinationSocketAddress(),
										config.getSourceSocketAddress()))
								.addLast(new LoggingHandler(LogLevel.DEBUG))
								// FINS
								.addLast(new FinsMasterResponseHandler(FinsNettyUdpMaster.this))
								;
					}
				})
				.connect(this.config.getDestinationSocketAddress(), this.config.getSourceSocketAddress())
				.addListener((ChannelFuture future) -> {
					if (future.isSuccess()) {
						this.channel = future.channel();
						masterFuture.complete(FinsNettyUdpMaster.this);
					} else {
						masterFuture.completeExceptionally(future.cause());
					}
				});
		return masterFuture;
	}

	public void disconnect() {
		this.config.getEventLoop().shutdownGracefully();
	}

	/*
	 * Functional API
	 * 
	 */

	@Override
	public Word readWord(FinsIoAddress address) throws FinsMasterException {
		return readWords(address, 1).get(0);
	}

	@Override
	public List<Word> readWords(final FinsIoAddress address, final int itemCount) throws FinsMasterException {		
		try {
			final MemoryAreaReadCommand<Word> command = new MemoryAreaReadCommand<>(address, itemCount);
			final CompletableFuture<MemoryAreaReadResponse<Word>> future = this.sendCommand(command);
			return future.get().getItems();
		} catch (InterruptedException | ExecutionException e) {
			throw new FinsMasterException("FINS master exception", e);
		}
	}
			
	@Override
	public Bit readBit(FinsIoAddress address) throws FinsMasterException {
		return this.readBits(address, 1).get(0);
	}
	
	@Override
	public List<Bit> readBits(final FinsIoAddress address, final int itemCount) throws FinsMasterException {
//		try {
			return null;
//		} catch (InterruptedException | ExecutionException e) {
//			throw new FinsMasterException("FINS master exception", e);
//		}
	}

	@Override
	public List<Word> readMultipleWords(final List<FinsIoAddress> addresses) throws FinsMasterException {
//		try {
			return null;
//		} catch (InterruptedException | ExecutionException e) {
//			throw new FinsMasterException("FINS master exception", e);
//		}
	}

	@Override
	public void writeBit(final FinsIoAddress address, final Bit item) throws FinsMasterException {
		this.writeBits(address, Collections.singletonList(item));
	}
	
	@Override
	public void writeBits(final FinsIoAddress address, final List<Bit> items) throws FinsMasterException {
		try {
			final MemoryAreaWriteCommand<Bit> command = new MemoryAreaWriteBitCommand(address, items);
			final CompletableFuture<FinsSimpleResponse> future = this.sendCommand(command);
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new FinsMasterException("FINS master exception", e);
		}
	}
	
	@Override
	public void writeWord(final FinsIoAddress address, final Word item) throws FinsMasterException {
		this.writeWords(address, Collections.singletonList(item));
	}

	@Override
	public void writeWords(final FinsIoAddress address, final List<Word> items) throws FinsMasterException {
		try {
			final MemoryAreaWriteCommand<Word> command = new MemoryAreaWriteWordCommand(address, items);
			final CompletableFuture<FinsSimpleResponse> future = this.sendCommand(command);
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new FinsMasterException("FINS master exception", e);
		}
	}

	@Override
	public <W extends Word> void writeMultipleWords(final List<FinsIoAddress> addresses, final List<W> items) throws FinsMasterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String readString(final FinsIoAddress address, final int wordLength) throws FinsMasterException {
		List<Word> words = this.readWords(address, wordLength);
		StringBuffer stringBuffer = new StringBuffer(wordLength * 2);
		byte[] bytes = new byte[2];
		for (Word w : words) {
			final Short s = w.getValue();
			bytes[1] = (byte)(s & 0xff);
			bytes[0] = (byte)((s >> 8) & 0xff);
			try {
				stringBuffer.append(new String(bytes, "US-ASCII"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}
	
	/*
	 * Internal
	 * 
	 */
	
	private <Response extends FinsResponse> CompletableFuture<Response> sendCommand(final FinsCommand<Response> command) {
		final CompletableFuture<Response> future = new CompletableFuture<>();
		final byte serviceAddress = (byte) this.serviceAddress.incrementAndGet();

		final Timeout timeout = this.config.getWheelTimer().newTimeout(t -> {
			if (t.isCancelled()) return;

			final PendingCommand<? extends FinsResponse> timedOut = pendingCommands.remove(serviceAddress);
			if (timedOut != null) {
				timedOut.promise.completeExceptionally(new FinsTimeoutException(config.getTimeout()));
			}
		}, this.config.getTimeout().getSeconds(), TimeUnit.SECONDS);

		this.pendingCommands.put(serviceAddress, new PendingCommand<Response>(future, timeout));

		final FinsHeader header = this.headerBuilder
				.responseAction(ResponseAction.RESPONSE_REQUIRED)
				.serviceAddress(serviceAddress)
				.build();
		
		this.channel.writeAndFlush(new FinsFrame(header, command)).addListener(f -> {
			if (!f.isSuccess()) {
				final PendingCommand<?> p = this.pendingCommands.remove(serviceAddress);
				if (p != null) {
					p.promise.completeExceptionally(f.cause());
					p.timeout.cancel();
				}
			}
		});
		return future;
	}

	public static class PendingCommand<Response extends FinsResponse> {
		private final CompletableFuture<Response> promise = new CompletableFuture<>();
		private final Timeout timeout;

		public PendingCommand(final CompletableFuture<Response> future, final Timeout timeout) {
			this.timeout = timeout;
			this.promise.whenComplete((response, exception) -> {
				if (response != null) {
					try {
						future.complete((Response) response);
					} catch (ClassCastException castException) {
						future.completeExceptionally(castException);
					}
				} else {
					future.completeExceptionally(exception);
				}
			});
		}
	}
	
	void onChannelRead(final ChannelHandlerContext context, final FinsFrame frame) throws Exception {
		if (frame.getPdu() instanceof FinsResponse) {
			@SuppressWarnings("unchecked")
			PendingCommand<FinsResponse> pending = (PendingCommand<FinsResponse>) pendingCommands.remove(frame.getHeader().getServiceAddress());
			
			if (pending != null) {
				pending.timeout.cancel();
				pending.promise.complete((FinsResponse) frame.getPdu());
			} else {
				ReferenceCountUtil.release(frame);
				logger.debug("Received response for unknown service address: {}", frame.getHeader().getServiceAddress());
			}
		} else {
			logger.error("Unexpected FINS PDU: {}", frame.getPdu());
		}
	}

	void onExceptionCaught(final ChannelHandlerContext context, final Throwable cause) throws Exception {
		logger.error("Exception caught: {}", cause.getMessage(), cause);
		failPendingRequests(cause);
		context.close();
	}

	private void failPendingRequests(final Throwable cause) {
		List<PendingCommand<?>> pending = new ArrayList<>(this.pendingCommands.values());
		pending.forEach(p -> p.promise.completeExceptionally(cause));
		this.pendingCommands.clear();
	}
	
	private static class FinsMasterResponseHandler extends SimpleChannelInboundHandler<FinsFrame> {
		
		private final FinsNettyUdpMaster master;

		private FinsMasterResponseHandler(final FinsNettyUdpMaster master) {
			this.master = master;
		}

		@Override
		protected void channelRead0(final ChannelHandlerContext context, final FinsFrame frame) throws Exception {
			this.master.onChannelRead(context, frame);
		}

		@Override
		public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) throws Exception {
			this.master.onExceptionCaught(context, cause);
		}

	}
	
}