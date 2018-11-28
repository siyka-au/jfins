package com.siyka.omron.fins.slave;

public interface FinsSlave {
	
	public void setHandler(ServiceCommandHandler handler);

	public ServiceCommandHandler getHandler();
	
}
