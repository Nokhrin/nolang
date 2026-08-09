package com.nokhrin.nolang.common.values;

public record BoolValue(boolean value) implements Value {
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
