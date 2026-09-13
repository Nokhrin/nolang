package com.nokhrin.nolang.common.values;

import java.util.function.Function;

public sealed interface Value permits NumericValue, Value.Bool, Value.Void {
    <T> T match(
        Function<NumericValue, T> onNumeric,
        Function<Bool, T> onBool,
        Function<Void, T> onVoid
    );

    record Void() implements Value {
        public static final Void INSTANCE = new Void();

        @Override
        public String toString() {
            return "void";
        }

        @Override
        public <T> T match(Function<NumericValue, T> onNumeric, Function<Bool, T> onBool, Function<Void, T> onVoid) {
            return onVoid.apply(this);
        }
    }

    record Bool(boolean value) implements Value {
        @Override
        public <T> T match(Function<NumericValue, T> onNumeric, Function<Bool, T> onBool, Function<Void, T> onVoid) {
            return onBool.apply(this);
        }

        @Override
        public String toString() {
            return Boolean.toString(value);
        }
    }
}
