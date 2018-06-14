package com.siyka.omron.fins.master;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.siyka.omron.fins.Bit;
import com.siyka.omron.fins.FinsIoAddress;

public interface FinsMaster {

	public CompletableFuture<FinsMaster> connect();

	public void disconnect();

	public Bit readBit(final FinsIoAddress address) throws FinsMasterException;

	public List<Bit> readBits(final FinsIoAddress address, final int itemCount) throws FinsMasterException;

	public short readWord(final FinsIoAddress address) throws FinsMasterException;

	public List<Short> readWords(final FinsIoAddress address, final int itemCount) throws FinsMasterException;

	public String readString(final FinsIoAddress address, final int wordLength) throws FinsMasterException;

	public List<Short> readMultipleWords(final List<FinsIoAddress> addresses) throws FinsMasterException;

	public void writeWord(final FinsIoAddress address, final short item) throws FinsMasterException;

	public void writeWords(final FinsIoAddress address, final List<Short> items) throws FinsMasterException;

	public void writeMultipleWords(final List<FinsIoAddress> addresses, final List<Short> items) throws FinsMasterException;

}
