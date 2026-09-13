package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;

public class Logical {

    public static Eval<Value.Bool> or(Value.Bool left, Value.Bool right) {
        return Eval.pure(new Value.Bool(left.value() || right.value()));
    }

    public static Eval<Value.Bool> and(Value.Bool left, Value.Bool right) {
        return Eval.pure(new Value.Bool(left.value() && right.value()));
    }

    public static Eval<Value.Bool> not(Value.Bool operand) {
        return Eval.pure(new Value.Bool(!operand.value()));
    }

    public static Eval<Value.Bool> compare(NumericValue left, ComparisonOperation op, NumericValue right) {
        boolean result =
            switch (op) {
                case GT -> greaterThan(left, right);
                case LT -> lessThan(left, right);
                case EQ -> equals(left, right);
                case NEQ -> !equals(left, right);
                case GEQ -> !lessThan(left, right);
                case LEQ -> !greaterThan(left, right);
            };
        return Eval.pure(new Value.Bool(result));
    }

    public static Eval<Value.Bool> compare(Value.Bool left, ComparisonOperation op, Value.Bool right) {
        return switch (op) {
            case EQ -> Eval.pure(new Value.Bool(left.value() == right.value()));
            case NEQ -> Eval.pure(new Value.Bool(left.value() != right.value()));
            default -> Eval.raiseError(new EvalError.ArithmeticError(
                "Comparison " + op + " is not defined for boolean values"));
        };
    }

    private static boolean equals(NumericValue left, NumericValue right) {
        return switch (left) {
            case NumericValue.Int l -> switch (right) {
                case NumericValue.Int r -> l.number() == r.number();
                case NumericValue.Real r -> l.number() == r.number();
            };
            case NumericValue.Real l -> switch (right) {
                case NumericValue.Int r -> l.number() == r.number();
                case NumericValue.Real r -> l.number() == r.number();
            };
        };
    }

    private static boolean greaterThan(NumericValue left, NumericValue right) {
        return switch (left) {
            case NumericValue.Int l -> switch (right) {
                case NumericValue.Int r -> l.number() > r.number();
                case NumericValue.Real r -> l.number() > r.number();
            };
            case NumericValue.Real l -> switch (right) {
                case NumericValue.Int r -> l.number() > r.number();
                case NumericValue.Real r -> l.number() > r.number();
            };
        };
    }

    private static boolean lessThan(NumericValue left, NumericValue right) {
        return switch (left) {
            case NumericValue.Int l -> switch (right) {
                case NumericValue.Int r -> l.number() < r.number();
                case NumericValue.Real r -> l.number() < r.number();
            };
            case NumericValue.Real l -> switch (right) {
                case NumericValue.Int r -> l.number() < r.number();
                case NumericValue.Real r -> l.number() < r.number();
            };
        };
    }
}
