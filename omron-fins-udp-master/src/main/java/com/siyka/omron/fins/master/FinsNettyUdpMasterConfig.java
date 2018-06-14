package com.siyka.omron.fins.master;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ExecutorService;

import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.codec.Fins;

import io.netty.channel.EventLoopGroup;
import io.netty.util.HashedWheelTimer;

public final class FinsNettyUdpMasterConfig {

	private final InetSocketAddress destinationSocketAddress;
	private final FinsNodeAddress destinationNodeAddress;
	private final InetSocketAddress sourceSocketAddress;
	private final FinsNodeAddress sourceNodeAddress;
	private final Duration timeout;

	private final ExecutorService executor;
	private final EventLoopGroup eventLoop;
	private final HashedWheelTimer wheelTimer;

	public FinsNettyUdpMasterConfig(
			final InetSocketAddress destinationSocketAddress,
			final FinsNodeAddress destinationNodeAddress,
			final InetSocketAddress sourceSocketAddress,
			final FinsNodeAddress sourceNodeAddress,
			final Duration timeout,
			final ExecutorService executor,
			final EventLoopGroup eventLoop,
			final HashedWheelTimer wheelTimer) {
		this.destinationSocketAddress = destinationSocketAddress;
		this.destinationNodeAddress = destinationNodeAddress;
		this.sourceSocketAddress = sourceSocketAddress;
		this.sourceNodeAddress = sourceNodeAddress;
		this.timeout = timeout;
		
		this.executor = executor;
		this.eventLoop = eventLoop;
		this.wheelTimer = wheelTimer;
	}

	public InetSocketAddress getDestinationSocketAddress() {
		return this.destinationSocketAddress;
	}

	public FinsNodeAddress getDestinationNodeAddress() {
		return this.destinationNodeAddress;
	}

	public InetSocketAddress getSourceSocketAddress() {
		return this.sourceSocketAddress;
	}

	public FinsNodeAddress getSourceNodeAddress() {
		return this.sourceNodeAddress;
	}

	public Duration getTimeout() {
		return this.timeout;
	}
	
	public ExecutorService getExecutor() {
		return this.executor;
	}
	
	public EventLoopGroup getEventLoop() {
		return this.eventLoop;
	}
	
	public HashedWheelTimer getWheelTimer() {
		return this.wheelTimer;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private InetSocketAddress destinationSocketAddress;
		private FinsNodeAddress destinationNodeAddress;
		private InetSocketAddress sourceSocketAddress;
		private FinsNodeAddress sourceNodeAddress;
		private Duration timeout = Duration.ofSeconds(3);

		private ExecutorService executor = Fins.sharedExecutor();
		private EventLoopGroup eventLoop = Fins.sharedEventLoop();
		private HashedWheelTimer wheelTimer = Fins.sharedWheelTimer();
		
		private Builder() { }
		
		public Builder destinationSocketAddress(final InetSocketAddress destinationSocketAddress) {
			this.destinationSocketAddress = destinationSocketAddress;
			return this;
		}

		public Builder destinationNodeAddress(final FinsNodeAddress destinationNodeAddress) {
			this.destinationNodeAddress = destinationNodeAddress;
			return this;
		}

		public Builder sourceSocketAddress(final InetSocketAddress sourceSocketAddress) {
			this.sourceSocketAddress = sourceSocketAddress;
			return this;
		}

		public Builder sourceNodeAddress(final FinsNodeAddress sourceNodeAddress) {
			this.sourceNodeAddress = sourceNodeAddress;
			return this;
		}

		public Builder executor(final ExecutorService executor) {
			this.executor = executor;
			return this;
		}

		public Builder group(final EventLoopGroup eventLoop) {
			this.eventLoop = eventLoop;
			return this;
		}
		
		public Builder wheelTimer(final HashedWheelTimer wheelTimer) {
			this.wheelTimer = wheelTimer;
			return this;
		}
		
		public FinsNettyUdpMasterConfig build() {
				return new FinsNettyUdpMasterConfig(
					this.destinationSocketAddress,
					this.destinationNodeAddress,
					this.sourceSocketAddress,
					this.sourceNodeAddress,
					this.timeout,
					this.executor,
					this.eventLoop,
					this.wheelTimer);
		}
		
	}

}
