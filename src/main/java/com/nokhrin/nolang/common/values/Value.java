package com.nokhrin.nolang.common.values;

import java.util.function.Function;

public sealed interface Value permits NumericValue, Value.BoolValue, Value.VoidValue {
  <T> T match(
      Function<NumericValue, T> onNumeric,
      Function<BoolValue, T> onBool,
      Function<VoidValue, T> onVoid);

  record VoidValue() implements Value {
    public static final VoidValue INSTANCE = new VoidValue();

    @Override
    public String toString() {
      return "void";
    }

    @Override
    public <T> T match(
        Function<NumericValue, T> onNumeric,
        Function<BoolValue, T> onBool,
        Function<VoidValue, T> onVoid) {
      return onVoid.apply(this);
    }
  }

  record BoolValue(boolean value) implements Value {
    @Override
    public <T> T match(
        Function<NumericValue, T> onNumeric,
        Function<BoolValue, T> onBool,
        Function<VoidValue, T> onVoid) {
      return onBool.apply(this);
    }

    @Override
    public String toString() {
      return Boolean.toString(value);
    }
  }
}
