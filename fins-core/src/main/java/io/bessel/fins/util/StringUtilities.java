package io.bessel.fins.util;

import javax.xml.bind.DatatypeConverter;

public class StringUtilities {
	public static String getHexString(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes).replaceAll(".{2}(?!$)", "$0 ");
	}
}
