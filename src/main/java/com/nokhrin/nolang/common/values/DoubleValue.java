package com.nokhrin.nolang.common.values;

import com.nokhrin.nolang.common.Type;

public record DoubleValue(double number) implements NumericValue {
  @Override
  public String toString() {
    return String.valueOf(number);
  }

  @Override
  public Type type() {
    return Type.FLOAT;
  }
}
