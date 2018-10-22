package com.siyka.omron.fins.master;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsNodeAddress;
import com.siyka.omron.fins.Word;

public interface FinsMaster {

	public CompletableFuture<Void> connect();

	public CompletableFuture<Void> disconnect();

	public CompletableFuture<List<Word>> readWords(final FinsNodeAddress destination, final FinsIoAddress address, final short itemCount);
	public CompletableFuture<List<Word>> readWords(final FinsNodeAddress destination, final FinsIoAddress address, final int itemCount);
	public CompletableFuture<Word> readWord(final FinsNodeAddress destination, final FinsIoAddress address);

	public CompletableFuture<byte[]> readBytes(final FinsNodeAddress destination, final FinsIoAddress address, final short itemCount);
	public CompletableFuture<byte[]> readBytes(final FinsNodeAddress destination, final FinsIoAddress address, final int itemCount);
	
	public CompletableFuture<String> readString(final FinsNodeAddress destination, final FinsIoAddress address, final short wordLength);
	public CompletableFuture<String> readString(final FinsNodeAddress destination, final FinsIoAddress address, final int wordLength);
	
//	public CompletableFuture<List<Bit>> readBits(final FinsNodeAddress destination, final FinsIoAddress address, final int itemCount);
//	public CompletableFuture<Bit> readBit(final FinsNodeAddress destination, final FinsIoAddress address);

//	public CompletableFuture<List<Word>> readMultipleWords(final FinsNodeAddress destination, final List<FinsIoAddress> addresses);

	public CompletableFuture<Void> writeWords(final FinsNodeAddress destination, final FinsIoAddress address, final List<Word> items);
	public CompletableFuture<Void> writeWord(final FinsNodeAddress destination, final FinsIoAddress address, final Word item);
	
//	public CompletableFuture<Void> writeMultipleWords(final FinsNodeAddress destination, final List<FinsIoAddress> addresses, final List<Short> items);




}
