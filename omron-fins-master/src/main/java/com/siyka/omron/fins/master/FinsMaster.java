package com.siyka.omron.fins.master;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsIoAddress;
import com.siyka.omron.fins.Word;

public interface FinsMaster {

	public CompletableFuture<FinsMaster> connect();

	public void disconnect();

	public Bit readBit(final FinsIoAddress address) throws FinsMasterException;

	public List<Bit> readBits(final FinsIoAddress address, final int itemCount) throws FinsMasterException;
	
	public Word readWord(final FinsIoAddress address) throws FinsMasterException;

	public List<Word> readWords(final FinsIoAddress address, final int itemCount) throws FinsMasterException;

	public String readString(final FinsIoAddress address, final int wordLength) throws FinsMasterException;

	public List<Word> readMultipleWords(final List<FinsIoAddress> addresses) throws FinsMasterException;

	public void writeBit(final FinsIoAddress address, final Bit item) throws FinsMasterException;
	
	public void writeBits(final FinsIoAddress address, final List<Bit> item) throws FinsMasterException;
	
	public void writeWord(final FinsIoAddress address, final Word item) throws FinsMasterException;

	public void writeWords(final FinsIoAddress address, final List<Word> items) throws FinsMasterException;

	public <W extends Word> void writeMultipleWords(final List<FinsIoAddress> addresses, final List<W> items) throws FinsMasterException;


}
