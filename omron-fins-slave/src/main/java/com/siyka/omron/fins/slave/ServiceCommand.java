package com.siyka.omron.fins.slave;

import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.responses.FinsResponse;

public interface ServiceCommand<Command extends FinsCommand<Response>, Response extends FinsResponse> {

    public Command getCommand();

    public void sendResponse(Response response);
    
}
