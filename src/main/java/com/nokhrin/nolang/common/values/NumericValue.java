package com.nokhrin.nolang.common.values;

public sealed interface NumericValue extends Value permits DoubleValue, IntValue {
  static NumericValue parse(String lexem) {
    if (lexem.contains(".")) {
      return new DoubleValue(Double.parseDouble(lexem));
    }
    return IntValue.parse(lexem);
  }
}
