package com.siyka.omron.fins.slave;

import java.util.concurrent.CompletableFuture;

public interface FinsSlave {

	public CompletableFuture<FinsSlave> bind(final String host, final int port);
	
	public void shutdown();
	
	public void setHandler(final ServiceCommandHandler handler);	
	
}
