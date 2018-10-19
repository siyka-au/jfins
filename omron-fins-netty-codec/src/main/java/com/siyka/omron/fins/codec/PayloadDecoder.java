package com.siyka.omron.fins.codec;

import com.siyka.omron.fins.FinsPdu;

@FunctionalInterface
public interface PayloadDecoder<T extends FinsPdu<T>> extends Decoder<T> {

}
