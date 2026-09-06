package com.nokhrin.nolang.common.values;

import java.util.function.Function;

public record IntValue(long number) implements NumericValue {
    @Override
    public String toString() {
        return String.valueOf(number);
    }

    @Override
    public <T> T match(Function<NumericValue, T> onNumeric, Function<BoolValue, T> onBool, Function<VoidValue, T> onVoid) {
        return onNumeric.apply(this);
    }
}
