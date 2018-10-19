package com.siyka.omron.fins.codec;

import com.siyka.omron.fins.FinsPdu;

@FunctionalInterface
public interface PayloadEncoder<T extends FinsPdu<T>> extends Encoder<T> {

}
