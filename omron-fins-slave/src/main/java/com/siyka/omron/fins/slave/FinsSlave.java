package com.siyka.omron.fins.slave;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public interface FinsSlave {

	public CompletableFuture<FinsSlave> bind(final int port);
	
	public CompletableFuture<FinsSlave> bind(final InetSocketAddress address);
	
	public CompletableFuture<FinsSlave> bind(final InetAddress address, final int port);
	
	public CompletableFuture<FinsSlave> bind(final String hostname, final int port);
	
	public CompletableFuture<Void> shutdown();
	
	public void setHandler(final ServiceCommandHandler handler);	
	
}
