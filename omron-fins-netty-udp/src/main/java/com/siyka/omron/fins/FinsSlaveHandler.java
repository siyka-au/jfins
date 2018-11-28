package com.siyka.omron.fins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.slave.ServiceCommandHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FinsSlaveHandler extends SimpleChannelInboundHandler<FinsCommand> {

	final static Logger logger = LoggerFactory.getLogger(FinsSlaveHandler.class);
	
	private final FinsNettyUdp fins;

	public FinsSlaveHandler(FinsNettyUdp fins) {
		this.fins = fins;
	}
	
    @Override
    protected void channelRead0(final ChannelHandlerContext context, final FinsCommand command) throws Exception {   	
		switch (command.getCommandCode()) {
    		case MEMORY_AREA_READ: {
    			fins.getHandler().onMemoryAreaRead(FinsNettyUdpSlaveServiceCommand.of(fins, (MemoryAreaReadCommand) command));
    		}
    		
    		case MEMORY_AREA_WRITE: {
    			fins.getHandler().onMemoryAreaWrite(FinsNettyUdpSlaveServiceCommand.of(fins, (MemoryAreaWriteCommand) command));
    		}
    		
    		default:
    	}
    }
    
	private static class FinsNettyUdpSlaveServiceCommand<Command extends FinsCommand, Response extends FinsResponse> implements ServiceCommandHandler.ServiceCommand<Command, Response> {
		private final FinsNettyUdp fins;
		private final Command command;
		
		private FinsNettyUdpSlaveServiceCommand(FinsNettyUdp fins, Command command) {
            this.fins = fins;
            this.command = command;
        }

		@Override
		public Command getCommand() {
			return command;
		}

		@Override
		public void respond(Response response) {
			fins.send(response);
		}
		
		public static <Command extends FinsCommand, Response extends FinsResponse> FinsNettyUdpSlaveServiceCommand<Command, Response> of(FinsNettyUdp fins, Command command) {
            return new FinsNettyUdpSlaveServiceCommand<>(fins, command);
        }
		
	}
    
}
