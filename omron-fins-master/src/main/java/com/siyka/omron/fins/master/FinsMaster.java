package com.siyka.omron.fins.master;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.Word;

public interface FinsMaster {

	public CompletableFuture<List<Word>> readWords(FinsIoAddress address, int itemCount);
	public CompletableFuture<Word> readWord(FinsIoAddress address);

	public CompletableFuture<byte[]> readBytes(FinsIoAddress address, int itemCount);
	
	public CompletableFuture<String> readString(FinsIoAddress address, int wordLength);
	
//	public CompletableFuture<List<Bit>> readBits(FinsIoAddress address, int itemCount);
//	public CompletableFuture<Bit> readBit(FinsIoAddress address);

//	public CompletableFuture<List<Word>> readMultipleWords(List<FinsIoAddress> addresses);

	public CompletableFuture<Void> writeBytes(FinsIoAddress address, List<Byte> bytes);
	public CompletableFuture<Void> writeBytes(FinsIoAddress address, byte... bytes);
	public CompletableFuture<Void> writeBytes(FinsIoAddress address, Byte... bytes);
	
	public CompletableFuture<Void> writeWords(FinsIoAddress address, List<Word> items);
	public CompletableFuture<Void> writeWords(FinsIoAddress address, Word... items);
	public CompletableFuture<Void> writeWord(FinsIoAddress address, Word item);

	public CompletableFuture<Void> writeBits(FinsIoAddress address, List<Bit> items);
	public CompletableFuture<Void> writeBits(FinsIoAddress address, Bit... items);
	public CompletableFuture<Void> writeBit(FinsIoAddress address, Bit value);
	
	public CompletableFuture<Void> writeString(FinsIoAddress address, String text, int maxLength);
	public CompletableFuture<Void> writeString(FinsIoAddress address, String text);
	
//	public CompletableFuture<Void> writeMultipleWords(List<FinsIoAddress> addresses, List<Short> items);

}
