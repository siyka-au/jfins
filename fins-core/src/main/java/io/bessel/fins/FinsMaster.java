package io.bessel.fins;

import java.util.List;

public interface FinsMaster extends AutoCloseable {

	public void connect() throws FinsMasterException;

	public void disconnect();

	public short readWord(FinsNodeAddress destination, FinsIoAddress address) throws FinsMasterException;

	public List<Short> readWords(FinsNodeAddress destination, FinsIoAddress address, short itemCount) throws FinsMasterException;

	public List<Short> readWords(FinsNodeAddress destination, FinsIoAddress address, int itemCount) throws FinsMasterException;

	public Bit readBit(FinsNodeAddress destination, FinsIoAddress address) throws FinsMasterException;

	public List<Bit> readBits(FinsNodeAddress destination, FinsIoAddress address, short itemCount) throws FinsMasterException;

	public List<Bit> readBits(FinsNodeAddress destination, FinsIoAddress address, int itemCount) throws FinsMasterException;

	public List<Short> readMultipleWords(FinsNodeAddress destination, List<FinsIoAddress> addresses) throws FinsMasterException;

	public void writeWord(FinsNodeAddress destination, FinsIoAddress address, short item) throws FinsMasterException;

	public void writeWords(FinsNodeAddress destination, FinsIoAddress address, List<Short> items) throws FinsMasterException;

	public void writeMultipleWords(FinsNodeAddress destination, List<FinsIoAddress> addresses, List<Short> items) throws FinsMasterException;

	public String readString(FinsNodeAddress destination, FinsIoAddress address, int wordLength) throws FinsMasterException;

	public String readString(FinsNodeAddress destination, FinsIoAddress address, short wordLength) throws FinsMasterException;

}
