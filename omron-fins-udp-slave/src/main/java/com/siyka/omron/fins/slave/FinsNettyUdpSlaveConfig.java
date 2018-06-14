package com.siyka.omron.fins.slave;

import java.net.InetSocketAddress;

import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.codec.Fins;

import io.netty.channel.EventLoopGroup;

public final class FinsNettyUdpSlaveConfig {

	private final InetSocketAddress socketAddress;
	private final FinsNodeAddress nodeAddress;
	private final EventLoopGroup eventLoop;

	protected FinsNettyUdpSlaveConfig(InetSocketAddress socketAddress, FinsNodeAddress nodeAddress, EventLoopGroup eventLoop) {
		this.socketAddress = socketAddress;
		this.nodeAddress = nodeAddress;
		this.eventLoop = eventLoop;
	}

	public InetSocketAddress getSocketAddress() {
		return this.socketAddress;
	}

	public FinsNodeAddress getNodeAddress() {
		return this.nodeAddress;
	}

	public EventLoopGroup getGroup() {
		return this.eventLoop;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {

		private InetSocketAddress socketAddress;
		private FinsNodeAddress nodeAddress;
		
		private EventLoopGroup eventLoop = Fins.sharedEventLoop();

		protected Builder() { }
		
		public Builder socketAddress(final InetSocketAddress socketAddress) {
			this.socketAddress = socketAddress;
			return this;
		}

		public Builder nodeAddress(final FinsNodeAddress nodeAddress) {
			this.nodeAddress = nodeAddress;
			return this;
		}

		public Builder group(final EventLoopGroup eventLoop) {
			this.eventLoop = eventLoop;
			return this;
		}

		public FinsNettyUdpSlaveConfig build() {
			return new FinsNettyUdpSlaveConfig(
					this.socketAddress,
					this.nodeAddress,
					this.eventLoop);
		}

	}

}
