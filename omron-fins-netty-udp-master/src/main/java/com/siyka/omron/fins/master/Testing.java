package com.siyka.omron.fins.master;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsIoMemoryArea;
import com.siyka.omron.fins.FinsNode;
import com.siyka.omron.fins.FinsNodeAddress;

public class Testing {

	private static final Logger logger = LoggerFactory.getLogger(Testing.class);

	
	public static void main(String[] args) throws InterruptedException, ExecutionException  {
		
		final FinsNode remote = new FinsNode(new InetSocketAddress("192.168.250.10", 9600), new FinsNodeAddress(0,  10,  0));
		final FinsNode local = new FinsNode(new InetSocketAddress("192.168.250.11", 9601), new FinsNodeAddress(0,  20,  0));

		final FinsMaster master = new FinsNettyUdpMaster(remote, local);
		
		logger.info("Connecting...");
		master.connect().get();
		logger.info("Connected!");
//		//short d = finsMaster.readWord(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 13000));
//		String s = finsMaster.readString(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 13000), 20);
//		System.out.println(String.format("%s", s.trim()));
		
		IntStream.range(1,  999)
				.forEach(i -> {
					
					try {
						logger.info("Sending write command");
						master.writeString(new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 10000), String.format("Hi %d", i));
//						master.writeWord(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 600, 0), new Bit(true));
//						master.writeBits(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_BIT, 600, 4), new Bit(true), new Bit(true), new Bit(true));
						
//						logger.info("Sending read command");
//						master.readString(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 10000), 20)
//								.thenApply(String::trim)
//								.thenApply(s -> String.format("'%s' %d",  s, s.length()))
//								.thenAccept(System.out::println)
//								.get();
						
						Thread.sleep(1000);
					} catch (final InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
//		List<Word> words = master.readWords(destNode, , 10).get();
////				.thenAccept(words -> {
//					logger.info("Received words");
//					int i = 0;
//					for (Word word : words) {
//						System.out.println(String.format("Word %d = %d", i, word.getValue()));
//						i++;
//					}
////				})
////				.thenRun(master::disconnect)
////				.get();
		master.disconnect().get();
		
	}

}
