package com.siyka.omron.fins.slave;

import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.responses.FinsSimpleResponse;

public interface ServiceCommandHandler {

	public default void onMemoryAreaWrite(ServiceCommand<MemoryAreaWriteCommand<?>, FinsSimpleResponse> service) {
        service.sendResponse(new FinsSimpleResponse(
        		service.getCommand().getCommandCode(),
        		FinsEndCode.NOT_SUPPORTED_BY_MODEL_VERSION));
    }
	
	public static interface ServiceCommand<Command extends FinsCommand, Response extends FinsResponse> {

	    public Command getCommand();

	    public void sendResponse(Response response);
	    
	}
	
}
