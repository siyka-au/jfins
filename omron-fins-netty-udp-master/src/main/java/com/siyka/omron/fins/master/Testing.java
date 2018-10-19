package com.siyka.omron.fins.master;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsIoMemoryArea;
import com.siyka.omron.fins.FinsNodeAddress;

public class Testing {

	private static final Logger logger = LoggerFactory.getLogger(Testing.class);

	
	public static void main(String[] args) throws InterruptedException, ExecutionException  {
		final FinsMaster master = new FinsNettyUdpMaster(
			new InetSocketAddress("192.168.250.10", 9600),
			new InetSocketAddress("0.0.0.0", 9601),
			new FinsNodeAddress(0,  2,  0)
		);
		
		FinsNodeAddress destNode = new FinsNodeAddress(0,  10,  0);
		
		logger.info("Connecting...");
		master.connect().get();
		logger.info("Connected!");
//		//short d = finsMaster.readWord(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 13000));
//		String s = finsMaster.readString(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 13000), 20);
//		System.out.println(String.format("%s", s.trim()));
		
		logger.info("Sending read command");
		List<Short> words = master.readWords(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 10000), 10).get();
//				.thenAccept(words -> {
					logger.info("Received words");
					int i = 0;
					for (Short word : words) {
						System.out.println(String.format("Word %d = %d", i, word));
						i++;
					}
//				})
//				.thenRun(master::disconnect)
//				.get();
		
	}

}
