package com.siyka.omron.fins.commands;

import com.siyka.omron.fins.FinsPdu;

public interface FinsCommand<T extends FinsCommand<T>> extends FinsPdu<T> {
	
}
