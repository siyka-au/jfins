package com.siyka.omron.fins;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.commands.MemoryAreaWriteCommand;
import com.siyka.omron.fins.master.FinsMaster;
import com.siyka.omron.fins.responses.SimpleResponse;
import com.siyka.omron.fins.slave.FinsSlave;
import com.siyka.omron.fins.slave.ServiceCommandHandler;

public class Testing implements ServiceCommandHandler {

	private static final Logger logger = LoggerFactory.getLogger(Testing.class);

	public static void main(String[] args) throws InterruptedException, ExecutionException  {
		
		final FinsNode remote = new FinsNode(new InetSocketAddress("192.168.250.10", 9600), new FinsNodeAddress(0,  10,  0));
		final FinsNode local = new FinsNode(new InetSocketAddress("192.168.250.3", 9600), new FinsNodeAddress(0,  20,  0));

		final FinsNettyUdp fins = new FinsNettyUdp(remote, local);
		
		FinsMaster master = fins;
		FinsSlave slave = fins;
		
		slave.setHandler(new Testing());
		
		logger.info("Connecting...");
		fins.connect().get();
		logger.info("Connected!");
		
		IntStream.range(1,  2)
				.forEach(i -> {
					
					try {
						logger.info("Sending write command");
						master.writeString(new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 10000), String.format("Hi %d", i)).get();
//						master.writeWord(new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 600, 0), new Bit(true));
//						master.writeBits(new FinsIoAddress(FinsIoMemoryArea.DM_BIT, 600, 4), new Bit(true), new Bit(true), new Bit(true));
						
						logger.info("Sending read command");
						master.readString(new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 10000), 20)
								.thenApply(String::trim)
								.thenApply(s -> String.format("'%s' %d",  s, s.length()))
								.thenAccept(System.out::println)
								.get();
						
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (final InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				});

//		master.disconnect().get();		
	}


	@Override
	public void onMemoryAreaWrite(ServiceCommand<MemoryAreaWriteCommand, SimpleResponse> service) {
		logger.info("Memory Area Write: {}", service.getCommand().getAddress());
		service.respond(new SimpleResponse(CommandCode.MEMORY_AREA_WRITE, ResponseCode.NORMAL_COMPLETION));
	}	
	
}
