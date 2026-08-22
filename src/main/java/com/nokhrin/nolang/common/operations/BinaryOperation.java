package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;
import java.util.function.BinaryOperator;

public enum BinaryOperation implements BinaryOperator<NumericValue> {
  ADD("+"),
  SUB("-"),
  MUL("*"),
  DIV("/"),
  POW("^");

  private final String operator;

  BinaryOperation(String operator) {
    this.operator = operator;
  }

  public static BinaryOperation fromSymbol(String operator) {
    return switch (operator) {
      case "+" -> ADD;
      case "-" -> SUB;
      case "*" -> MUL;
      case "/" -> DIV;
      case "^" -> POW;
      default -> throw new IllegalArgumentException("Unknown binary operator: " + operator);
    };
  }

  @Override
  public NumericValue apply(NumericValue left, NumericValue right) {
    return switch (this) {
      case ADD -> Arithmetic.add(left, right);
      case SUB -> Arithmetic.sub(left, right);
      case MUL -> Arithmetic.mul(left, right);
      case DIV -> Arithmetic.div(left, right);
      case POW -> Arithmetic.pow(left, right);
    };
  }
}
