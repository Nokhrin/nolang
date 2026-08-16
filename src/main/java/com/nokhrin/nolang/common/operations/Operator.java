package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.Value;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class Operator {
  private Operator() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static Value applyBinary(String op, Value left, Value right) {
    BinaryOperator<Value> operation =
        switch (op) {
          case "+" -> Arithmetic::add;
          case "-" -> Arithmetic::sub;
          case "*" -> Arithmetic::mul;
          case "/" -> Arithmetic::div;
          case "^" -> Arithmetic::pow;
          default -> throw new IllegalArgumentException("Unexpected binary operation");
        };
    return operation.apply(left, right);
  }

  public static Value applyUnary(String op, Value operand) {
    UnaryOperator<Value> operation =
        switch (op) {
          case "+" -> UnaryOperator.identity();
          case "-" -> Arithmetic::neg;
          default -> throw new IllegalArgumentException("Unexpected unary operation");
        };
    return operation.apply(operand);
  }

  public static Value applyFactorial(Value operand) {
    UnaryOperator<Value> operation = Arithmetic::fact;
    return operation.apply(operand);
  }

  public static Value applyAbs(Value operand) {
    UnaryOperator<Value> operation = Arithmetic::abs;
    return operation.apply(operand);
  }
}
