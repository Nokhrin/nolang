package com.nokhrin.nolang.common.values;

import java.util.function.Function;

public record RealValue(double number) implements NumericValue {
    @Override
    public <T> T match(Function<NumericValue, T> onNumeric, Function<BoolValue, T> onBool, Function<VoidValue, T> onVoid) {
        return onNumeric.apply(this);
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }
}
