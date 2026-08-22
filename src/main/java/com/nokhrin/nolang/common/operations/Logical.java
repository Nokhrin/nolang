package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.*;

public class Logical {

  public enum LogicalOperation {
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

    public static LogicalOperation fromSymbol(String symbol) {
      return switch (symbol) {
        case ">" -> GT;
        case "<" -> LT;
        case "==" -> EQ;
        case "!=" -> NEQ;
        case ">=" -> GEQ;
        case "<=" -> LEQ;
        default -> throw new IllegalArgumentException("Unknown comparison operator");
      };
    }
  }

  private Logical() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static BoolValue or(Value left, Value right) {
    return switch (left) {
      case BoolValue(boolean l) ->
          switch (right) {
            case BoolValue(boolean r) -> new BoolValue(l || r);
            case IntValue _, RealValue _ ->
                throw new IllegalStateException("Cannot apply to non-bool: " + right);
          };
      case IntValue _, RealValue _ ->
          throw new IllegalStateException("Cannot apply to non-bool: " + left);
    };
  }

  public static BoolValue and(Value left, Value right) {
    return switch (left) {
      case BoolValue(boolean l) ->
          switch (right) {
            case BoolValue(boolean r) -> new BoolValue(l && r);
            case IntValue _, RealValue _ ->
                throw new IllegalStateException("Cannot apply to non-bool: " + right);
          };
      case IntValue _, RealValue _ ->
          throw new IllegalStateException("Cannot apply to non-bool: " + left);
    };
  }

  public static BoolValue not(Value operand) {
    return switch (operand) {
      case BoolValue(boolean op) -> new BoolValue(!op);
      case IntValue _, RealValue _ ->
          throw new IllegalStateException("Cannot apply to non-bool: " + operand);
    };
  }

  public static BoolValue compare(NumericValue left, LogicalOperation op, NumericValue right) {
    boolean result =
        switch (op) {
          case GT -> greaterThan(left, right);
          case LT -> lessThan(left, right);
          case EQ -> equals(left, right);
          case NEQ -> !equals(left, right);
          case GEQ -> !lessThan(left, right);
          case LEQ -> !greaterThan(left, right);
        };
    return new BoolValue(result);
  }

  private static boolean equals(NumericValue left, NumericValue right) {
    return switch (left) {
      case IntValue l ->
          switch (right) {
            case IntValue r -> l.number() == r.number();
            case RealValue r -> l.number() == r.number();
          };
      case RealValue l ->
          switch (right) {
            case IntValue r -> l.number() == r.number();
            case RealValue r -> l.number() == r.number();
          };
    };
  }

  private static boolean greaterThan(NumericValue left, NumericValue right) {
    return switch (left) {
      case IntValue l ->
          switch (right) {
            case IntValue r -> l.number() > r.number();
            case RealValue r -> l.number() > r.number();
          };
      case RealValue l ->
          switch (right) {
            case IntValue r -> l.number() > r.number();
            case RealValue r -> l.number() > r.number();
          };
    };
  }

  private static boolean lessThan(NumericValue left, NumericValue right) {
    return switch (left) {
      case IntValue l ->
          switch (right) {
            case IntValue r -> l.number() < r.number();
            case RealValue r -> l.number() < r.number();
          };
      case RealValue l ->
          switch (right) {
            case IntValue r -> l.number() < r.number();
            case RealValue r -> l.number() < r.number();
          };
    };
  }
}
