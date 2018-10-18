package com.siyka.omron.fins.master;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.FinsNodeAddress;

public interface FinsMaster extends AutoCloseable {

	public CompletableFuture<Void> connect();

	public CompletableFuture<Void> disconnect();

	public CompletableFuture<Short> readWord(FinsNodeAddress destination, FinsIoAddress address);

	public CompletableFuture<List<Short>> readWords(FinsNodeAddress destination, FinsIoAddress address, short itemCount);

	public CompletableFuture<List<Short>> readWords(FinsNodeAddress destination, FinsIoAddress address, int itemCount);

	public CompletableFuture<Bit> readBit(FinsNodeAddress destination, FinsIoAddress address);

	public CompletableFuture<List<Bit>> readBits(FinsNodeAddress destination, FinsIoAddress address, short itemCount);

	public CompletableFuture<List<Bit>> readBits(FinsNodeAddress destination, FinsIoAddress address, int itemCount);

	public CompletableFuture<List<Short>> readMultipleWords(FinsNodeAddress destination, List<FinsIoAddress> addresses);

	public CompletableFuture<Void> writeWord(FinsNodeAddress destination, FinsIoAddress address, short item);

	public CompletableFuture<Void> writeWords(FinsNodeAddress destination, FinsIoAddress address, List<Short> items);

	public CompletableFuture<Void> writeMultipleWords(FinsNodeAddress destination, List<FinsIoAddress> addresses, List<Short> items);

	public CompletableFuture<String> readString(FinsNodeAddress destination, FinsIoAddress address, int wordLength);

	public CompletableFuture<String> readString(FinsNodeAddress destination, FinsIoAddress address, short wordLength);

}
