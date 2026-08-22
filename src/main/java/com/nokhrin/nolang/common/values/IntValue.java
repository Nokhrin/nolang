package com.nokhrin.nolang.common.values;

public record IntValue(long number) implements NumericValue {
  @Override
  public String toString() {
    return String.valueOf(number);
  }
}
