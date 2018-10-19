package com.siyka.omron.fins;

public interface FinsPdu<T extends FinsPdu<T>> {

	public FinsCommandCode getCommandCode();
	
//	public byte[] getPayload();
		
}
