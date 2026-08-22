package com.nokhrin.nolang.common.values;

public record RealValue(double number) implements NumericValue {
  @Override
  public String toString() {
    return String.valueOf(number);
  }
}
