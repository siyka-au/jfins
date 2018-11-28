package com.siyka.omron.fins;

public class ByteAlign {

	public static byte[] align(byte[] data) {
		if ((data.length & 0x01) != 0 ) {
			byte[] newData = new byte[data.length + 1];
			System.arraycopy(data, 0, newData, 0, data.length);
			return newData;
		}
		
		return data;
	}
	
}
