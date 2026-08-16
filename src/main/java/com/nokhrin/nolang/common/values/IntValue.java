package com.nokhrin.nolang.common.values;

import com.nokhrin.nolang.common.Type;

public record IntValue(long number) implements NumericValue {
  @Override
  public String toString() {
    return String.valueOf(number);
  }

  @Override
  public Type type() {
    return Type.INTEGER;
  }

  public static IntValue parse(long n) {
    return new IntValue(n);
  }

  public static IntValue parse(String n) {
    return new IntValue(Long.parseLong(n));
  }
}
