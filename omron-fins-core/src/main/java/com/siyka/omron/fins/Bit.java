package com.siyka.omron.fins;

public class Bit implements DataType<Boolean> {

	private static final long serialVersionUID = -1706234047187405546L;
	
	private final boolean value;

	public Bit(final boolean value) {
		this.value = value;
	}

	public Boolean getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (value ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bit other = (Bit) obj;
		if (value != other.value)
			return false;
		return true;
	}

}
