package com.siyka.omron.fins.slave;

import com.siyka.omron.fins.FinsCommand;
import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.FinsResponse;
import com.siyka.omron.fins.MemoryAreaWriteCommand;
import com.siyka.omron.fins.SimpleResponse;

public interface ServiceCommandHandler {

	public default void onMemoryAreaWrite(ServiceCommand<MemoryAreaWriteCommand<?>, SimpleResponse> service) {
        service.sendResponse(new SimpleResponse(
        		service.getCommand().getCommandCode(),
        		FinsEndCode.NOT_SUPPORTED_BY_MODEL_VERSION));
    }
	
	public static interface ServiceCommand<Command extends FinsCommand, Response extends FinsResponse> {

	    public Command getCommand();

	    public void sendResponse(Response response);
	    
	}
	
}
