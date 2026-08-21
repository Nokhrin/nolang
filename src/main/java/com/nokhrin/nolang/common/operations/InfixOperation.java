package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;

import java.util.function.UnaryOperator;

public enum InfixOperation implements UnaryOperator<NumericValue> {
    ABSOLUTE;

    @Override
    public NumericValue apply(NumericValue number) {
        return Arithmetic.abs(number);
    }
}
