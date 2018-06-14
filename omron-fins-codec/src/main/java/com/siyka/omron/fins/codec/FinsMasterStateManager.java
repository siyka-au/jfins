package com.siyka.omron.fins.codec;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.siyka.omron.fins.FinsFrame;

public final class FinsMasterStateManager {

	private final Cache<Byte, FinsFrame> outgiongRequests;
	
	public FinsMasterStateManager() {
		this.outgiongRequests = CacheBuilder.newBuilder()
				.expireAfterAccess(1, TimeUnit.MINUTES)
				.build();
	}
	
	public void putFinsFrame(final byte serviceAddress, final FinsFrame frame) {
		this.outgiongRequests.put(serviceAddress, frame);
	}
	
	public FinsFrame getByServiceByte(final byte serviceAddress) {
		return this.outgiongRequests.getIfPresent(serviceAddress);
	}
	
}
