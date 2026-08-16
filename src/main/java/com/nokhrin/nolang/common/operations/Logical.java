package com.nokhrin.nolang.common.operations;

import static com.nokhrin.nolang.common.operations.Arithmetic.toDouble;

import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.DoubleValue;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.Value;

public class Logical {

  enum LogicalOperation {
    GT(">"),
    LT("<"),
    EQ("=="),
    NEQ("!="),
    GEQ(">="),
    LEQ("<=");

    private final String symbol;

    LogicalOperation(String symbol) {
      this.symbol = symbol;
    }
  }

  private Logical() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static Value or(Value left, Value right) {
    return switch (left) {
      case BoolValue(boolean l) ->
          switch (right) {
            case BoolValue(boolean r) -> new BoolValue(l || r);
            case IntValue _, DoubleValue _ ->
                throw new IllegalStateException("Cannot apply to non-bool: " + right);
          };
      case IntValue _, DoubleValue _ ->
          throw new IllegalStateException("Cannot apply to non-bool: " + left);
    };
  }

  public static Value and(Value left, Value right) {
    return switch (left) {
      case BoolValue(boolean l) ->
          switch (right) {
            case BoolValue(boolean r) -> new BoolValue(l && r);
            case IntValue _, DoubleValue _ ->
                throw new IllegalStateException("Cannot apply to non-bool: " + right);
          };
      case IntValue _, DoubleValue _ ->
          throw new IllegalStateException("Cannot apply to non-bool: " + left);
    };
  }

  public static Value not(Value operand) {
    return switch (operand) {
      case BoolValue(boolean op) -> new BoolValue(!op);
      case IntValue _, DoubleValue _ ->
          throw new IllegalStateException("Cannot apply to non-bool: " + operand);
    };
  }

  public static boolean equals(Value left, Value right) {
    return switch (left) {
      case IntValue(long l) ->
          switch (right) {
            case IntValue(long r) -> l == r;
            case DoubleValue(double r) -> l == r;
            case BoolValue _ -> false;
          };
      case DoubleValue(double l) ->
          switch (right) {
            case IntValue(long r) -> l == r;
            case DoubleValue(double r) -> l == r;
            case BoolValue _ -> false;
          };
      case BoolValue(boolean l) ->
          switch (right) {
            case BoolValue(boolean r) -> l == r;
            case IntValue _, DoubleValue _ -> false;
          };
    };
  }

  public static Value compare(Value left, String op, Value right) {
    boolean Value =
        switch (op) {
          case "==" -> equals(left, right);
          case "!=" -> !equals(left, right);
          case ">" -> toDouble(left) > toDouble(right);
          case "<" -> toDouble(left) < toDouble(right);
          case ">=" -> toDouble(left) >= toDouble(right);
          case "<=" -> toDouble(left) <= toDouble(right);
          default -> throw new IllegalStateException("Unexpected operator: " + op);
        };
    return new BoolValue(Value);
  }
}
