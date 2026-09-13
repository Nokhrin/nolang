package com.nokhrin.nolang.common.values;

import java.util.function.Function;

public sealed interface NumericValue extends Value permits NumericValue.Int, NumericValue.Real {
    record Int(long number) implements NumericValue {
        @Override
        public String toString() {
            return String.valueOf(number);
        }

        @Override
        public <T> T match(Function<NumericValue, T> onNumeric, Function<Bool, T> onBool, Function<Void, T> onVoid) {
            return onNumeric.apply(this);
        }
    }

    record Real(double number) implements NumericValue {
        @Override
        public <T> T match(Function<NumericValue, T> onNumeric, Function<Bool, T> onBool, Function<Void, T> onVoid) {
            return onNumeric.apply(this);
        }

        @Override
        public String toString() {
            return String.valueOf(number);
        }
    }
}
