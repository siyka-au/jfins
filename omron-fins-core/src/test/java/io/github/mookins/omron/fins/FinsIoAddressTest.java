package io.github.mookins.omron.fins;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.github.mookins.omron.fins.FinsIoAddress;
import io.github.mookins.omron.fins.FinsIoMemoryArea;

public class FinsIoAddressTest {

	@Test
	public void testOrdinalParser() {
		FinsIoAddress fia = FinsIoAddress.parseFrom(0x82112200);
		assertEquals("Memory code should be DM_WORD", FinsIoMemoryArea.DM_WORD, fia.getMemoryArea());
		assertEquals("Address should be 0x1122", 0x1122, fia.getAddress());
		assertEquals("Bit offset should be 0x00", 0x00, fia.getBitOffset());
	}
}
