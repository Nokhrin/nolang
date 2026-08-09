package com.nokhrin.nolang.common.values;

public record DoubleValue(double number) implements Value {
    @Override
    public String toString() {
        return String.valueOf(number);
    }
}
