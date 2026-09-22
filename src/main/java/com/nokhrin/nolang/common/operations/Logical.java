package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

public class Logical {

  public static Eval<Value.BoolValue> or(Value.BoolValue left, Value.BoolValue right) {
    return Eval.pure(new Value.BoolValue(left.value() || right.value()));
  }

  public static Eval<Value.BoolValue> and(Value.BoolValue left, Value.BoolValue right) {
    return Eval.pure(new Value.BoolValue(left.value() && right.value()));
  }

  public static Eval<Value.BoolValue> not(Value.BoolValue operand) {
    return Eval.pure(new Value.BoolValue(!operand.value()));
  }

  public static Eval<Value.BoolValue> compare(
      NumericValue left, NumericComparisonOperation op, NumericValue right) {
    boolean result =
        switch (op) {
          case GT -> greaterThan(left, right);
          case LT -> lessThan(left, right);
          case EQ -> equals(left, right);
          case NEQ -> !equals(left, right);
          case GEQ -> !lessThan(left, right);
          case LEQ -> !greaterThan(left, right);
        };
    return Eval.pure(new Value.BoolValue(result));
  }

  public static Eval<Value.BoolValue> compare(
      Value.BoolValue left, NumericComparisonOperation op, Value.BoolValue right) {
    return switch (op) {
      case EQ -> Eval.pure(new Value.BoolValue(left.value() == right.value()));
      case NEQ -> Eval.pure(new Value.BoolValue(left.value() != right.value()));
      default ->
          Eval.raiseError(
              new EvalError.ArithmeticError(
                  "Comparison " + op + " is not defined for boolean values"));
    };
  }

  private static boolean equals(NumericValue left, NumericValue right) {
    return switch (left) {
      case NumericValue.IntValue l ->
          switch (right) {
            case NumericValue.IntValue r -> l.number() == r.number();
            case NumericValue.RealValue r -> l.number() == r.number();
          };
      case NumericValue.RealValue l ->
          switch (right) {
            case NumericValue.IntValue r -> l.number() == r.number();
            case NumericValue.RealValue r -> l.number() == r.number();
          };
    };
  }

  private static boolean greaterThan(NumericValue left, NumericValue right) {
    return switch (left) {
      case NumericValue.IntValue l ->
          switch (right) {
            case NumericValue.IntValue r -> l.number() > r.number();
            case NumericValue.RealValue r -> l.number() > r.number();
          };
      case NumericValue.RealValue l ->
          switch (right) {
            case NumericValue.IntValue r -> l.number() > r.number();
            case NumericValue.RealValue r -> l.number() > r.number();
          };
    };
  }

  private static boolean lessThan(NumericValue left, NumericValue right) {
    return switch (left) {
      case NumericValue.IntValue l ->
          switch (right) {
            case NumericValue.IntValue r -> l.number() < r.number();
            case NumericValue.RealValue r -> l.number() < r.number();
          };
      case NumericValue.RealValue l ->
          switch (right) {
            case NumericValue.IntValue r -> l.number() < r.number();
            case NumericValue.RealValue r -> l.number() < r.number();
          };
    };
  }
}
