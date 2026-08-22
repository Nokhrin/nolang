package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;
import java.util.function.UnaryOperator;

public enum PostfixOperation implements UnaryOperator<NumericValue> {
  FACTORIAL("!");

  private final String operator;

  PostfixOperation(String operator) {
    this.operator = operator;
  }

  public static PostfixOperation fromSymbol(String operator) {
    return switch (operator) {
      case "!" -> FACTORIAL;
      default -> throw new IllegalArgumentException("Unknown unary operator: " + operator);
    };
  }

  @Override
  public NumericValue apply(NumericValue number) {
    return switch (this) {
      case FACTORIAL -> Arithmetic.fact(number);
    };
  }
}
