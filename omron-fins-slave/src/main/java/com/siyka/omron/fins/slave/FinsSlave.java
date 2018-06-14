package com.siyka.omron.fins.slave;

import java.util.concurrent.CompletableFuture;

public interface FinsSlave {

	public CompletableFuture<FinsSlave> bind();

	public void shutdown();

	public void setHandler(final ServiceCommandHandler handler);

}
