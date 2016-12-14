package io.github.mookins.omron.fins.master;

import java.net.InetSocketAddress;
import java.util.List;

import io.github.mookins.omron.fins.Bit;
import io.github.mookins.omron.fins.FinsIoAddress;
import io.github.mookins.omron.fins.FinsIoMemoryArea;
import io.github.mookins.omron.fins.FinsMasterException;
import io.github.mookins.omron.fins.FinsNodeAddress;

public class Testing {

	public static void main(String[] args) throws FinsMasterException {
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
		
		List<Bit> bits = finsMaster.readBits(destNode, new FinsIoAddress(FinsIoMemoryArea.DM_BIT, 20000), 4);
		
		int i = 0;
		for (Bit bit : bits) {
			System.out.println(String.format("Bit %d = %b", i, bit.getValue()));
			i++;
		}
		
		finsMaster.disconnect();
	}

}
