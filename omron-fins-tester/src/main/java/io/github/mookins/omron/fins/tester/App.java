package io.github.mookins.omron.fins.tester;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

import io.github.mookins.omron.fins.Bit;
import io.github.mookins.omron.fins.FinsIoAddress;
import io.github.mookins.omron.fins.FinsIoMemoryArea;
import io.github.mookins.omron.fins.FinsMasterException;
import io.github.mookins.omron.fins.FinsNodeAddress;
import io.github.mookins.omron.fins.master.FinsNettyUdpMaster;

public class App {

	public static void main(String... args) throws FinsMasterException {
		FinsNettyUdpMaster finsMaster = new FinsNettyUdpMaster(
			new InetSocketAddress("192.168.250.10", 9600),
			new InetSocketAddress("0.0.0.0", 9601),
			new FinsNodeAddress(0,  2,  0)
		);
		
		FinsNodeAddress destNode = new FinsNodeAddress(0,  10,  0);
		
		finsMaster.connect();
//		//short d = finsMaster.readWord(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 13000));
//		String s = finsMaster.readString(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, 13000), 20);
//		System.out.println(String.format("%s", s.trim()));
		
		Integer addr = Integer.parseInt(args[1]);
		Integer num = Integer.parseInt(args[2]);
		List<Short> words = finsMaster.readWords(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_WORD, addr), num);
		
		System.out.println(String.format("Read %d words starting at address %d", num, addr));
		System.out.println(words.stream().map(s -> s.toString()).collect(Collectors.joining(", ")));
		
		finsMaster.disconnect();
	}

}
