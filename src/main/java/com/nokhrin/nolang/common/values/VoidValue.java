package com.nokhrin.nolang.common.values;

import java.util.function.Function;

public record VoidValue() implements Value {
    public static final VoidValue INSTANCE = new VoidValue();

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public <T> T match(Function<NumericValue, T> onNumeric, Function<BoolValue, T> onBool, Function<VoidValue, T> onVoid) {
        return onVoid.apply(this);
    }
}
