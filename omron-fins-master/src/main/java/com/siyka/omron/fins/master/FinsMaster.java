package com.siyka.omron.fins.master;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.Word;

public interface FinsMaster {

	public CompletableFuture<Void> connect();

	public CompletableFuture<Void> disconnect();

	public CompletableFuture<List<Word>> readWords(final FinsIoAddress address, final int itemCount);
	public CompletableFuture<Word> readWord(final FinsIoAddress address);

	public CompletableFuture<byte[]> readBytes(final FinsIoAddress address, final int itemCount);
	
	public CompletableFuture<String> readString(final FinsIoAddress address, final int wordLength);
	
//	public CompletableFuture<List<Bit>> readBits(final FinsIoAddress address, final int itemCount);
//	public CompletableFuture<Bit> readBit(final FinsIoAddress address);

//	public CompletableFuture<List<Word>> readMultipleWords(final List<FinsIoAddress> addresses);

	public CompletableFuture<Void> writeWords(final FinsIoAddress address, final List<Word> items);
	public CompletableFuture<Void> writeWords(final FinsIoAddress address, final Word... items);
	public CompletableFuture<Void> writeWord(final FinsIoAddress address, final Word item);

	public CompletableFuture<Void> writeBits(final FinsIoAddress address, final List<Bit> items);
	public CompletableFuture<Void> writeBits(final FinsIoAddress address, final Bit... items);
	public CompletableFuture<Void> writeBit(final FinsIoAddress address, final Bit value);

	public CompletableFuture<Void> writeBytes(final FinsIoAddress address, final List<Byte> bytes);
	public CompletableFuture<Void> writeBytes(final FinsIoAddress address, final byte... bytes);
	public CompletableFuture<Void> writeBytes(final FinsIoAddress address, final Byte... bytes);
	
	public CompletableFuture<Void> writeString(final FinsIoAddress address, final String text, final int maxLength);
	public CompletableFuture<Void> writeString(final FinsIoAddress address, final String text);
	
//	public CompletableFuture<Void> writeMultipleWords(final List<FinsIoAddress> addresses, final List<Short> items);




}
