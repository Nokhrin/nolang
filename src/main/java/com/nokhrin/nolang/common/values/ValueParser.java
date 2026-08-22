package com.nokhrin.nolang.common.values;

public class ValueParser {
  public static NumericValue parseNumber(String lexeme) {
    if (lexeme.contains(".")) {
      return new RealValue(Double.parseDouble(lexeme));
    }
    return new IntValue(Long.parseLong(lexeme));
  }
}
