package com.siyka.omron.fins.slave;

import com.siyka.omron.fins.ResponseCode;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.responses.SimpleResponse;

public interface ServiceCommandHandler {

	public default void onMemoryAreaRead(ServiceCommand<MemoryAreaReadCommand, ? super SimpleResponse> service) {
        service.respond(new SimpleResponse(
        		service.getCommand().getCommandCode(),
        		ResponseCode.NOT_SUPPORTED_BY_MODEL_VERSION));
    }
	
	public default void onMemoryAreaWrite(ServiceCommand<MemoryAreaWriteCommand, SimpleResponse> service) {
        service.respond(new SimpleResponse(
        		service.getCommand().getCommandCode(),
        		ResponseCode.NOT_SUPPORTED_BY_MODEL_VERSION));
    }
	
	public static interface ServiceCommand<Command extends FinsCommand, Response extends FinsResponse> {

	    public Command getCommand();

	    public void respond(Response response);
	    
	}
	
}
