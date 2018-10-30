package com.siyka.omron.fins;

import java.net.InetSocketAddress;

public class FinsNode {

	private final InetSocketAddress socketAddress;
	private final FinsNodeAddress nodeAddress;

	public FinsNode(InetSocketAddress socketAddress, FinsNodeAddress nodeAddress) {
		super();
		this.socketAddress = socketAddress;
		this.nodeAddress = nodeAddress;
	}

	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

	public FinsNodeAddress getNodeAddress() {
		return nodeAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeAddress == null) ? 0 : nodeAddress.hashCode());
		result = prime * result + ((socketAddress == null) ? 0 : socketAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FinsNode other = (FinsNode) obj;
		if (nodeAddress == null) {
			if (other.nodeAddress != null)
				return false;
		} else if (!nodeAddress.equals(other.nodeAddress))
			return false;
		if (socketAddress == null) {
			if (other.socketAddress != null)
				return false;
		} else if (!socketAddress.equals(other.socketAddress))
			return false;
		return true;
	}

}
