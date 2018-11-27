package com.siyka.omron.fins.slave;

import com.siyka.omron.fins.EndCode;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.responses.SimpleResponse;

public interface ServiceCommandHandler {

	public default void onMemoryAreaWrite(ServiceCommand<MemoryAreaWriteCommand<?>, SimpleResponse> service) {
        service.sendResponse(new SimpleResponse(
        		service.getCommand().getCommandCode(),
        		EndCode.NOT_SUPPORTED_BY_MODEL_VERSION));
    }
	
	public static interface ServiceCommand<Command extends Command, Response extends Response> {

	    public Command getCommand();

	    public void sendResponse(Response response);
	    
	}
	
}
