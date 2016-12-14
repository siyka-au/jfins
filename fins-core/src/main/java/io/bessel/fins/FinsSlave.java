package io.bessel.fins;

import java.util.Optional;

public interface FinsSlave extends AutoCloseable {

	public void start() throws FinsSlaveException;
	
	public void shutdown();
	
	public void setMemoryAreaWriteHandler(MemoryAreaWriteCommandHandler handler);
	
	public Optional<MemoryAreaWriteCommandHandler> getMemoryAreaWriteHandler();
	
}
