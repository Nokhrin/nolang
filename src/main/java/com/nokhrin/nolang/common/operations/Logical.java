package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.*;
import com.nokhrin.nolang.functional.Eval;
import com.nokhrin.nolang.functional.EvalError;

public class Logical {

    public static Eval<BoolValue> or(Value left, Value right) {
        return switch (left) {
            case BoolValue(boolean l) -> switch (right) {
                case BoolValue(boolean r) -> Eval.pure(new BoolValue(l || r));
                case IntValue _, RealValue _, VoidValue _ -> Eval.raiseError(new EvalError.ArithmeticError("Cannot " +
                    "apply to non-bool: " + right));
            };
            case IntValue _, RealValue _, VoidValue _ -> Eval.raiseError(new EvalError.ArithmeticError("Cannot apply " +
                "to non-bool: " + left));
        };
    }

    public static Eval<BoolValue> and(Value left, Value right) {
        return switch (left) {
            case BoolValue(boolean l) -> switch (right) {
                case BoolValue(boolean r) -> Eval.pure(new BoolValue(l && r));
                case IntValue _, RealValue _, VoidValue _ -> Eval.raiseError(new EvalError.ArithmeticError("Cannot " +
                    "apply to non-bool: " + right));
            };
            case IntValue _, RealValue _, VoidValue _ -> Eval.raiseError(new EvalError.ArithmeticError("Cannot apply " +
                "to non-bool: " + left));
        };
    }

    public static Eval<BoolValue> not(Value operand) {
        return switch (operand) {
            case BoolValue(boolean op) -> Eval.pure(new BoolValue(!op));
            case IntValue _, RealValue _, VoidValue _ -> Eval.raiseError(new EvalError.ArithmeticError("Cannot apply " +
                "to non-bool: " + operand));
        };
    }

    public static Eval<BoolValue> compare(NumericValue left, ComparisonOperation op, NumericValue right) {
        boolean result =
            switch (op) {
                case GT -> greaterThan(left, right);
                case LT -> lessThan(left, right);
                case EQ -> equals(left, right);
                case NEQ -> !equals(left, right);
                case GEQ -> !lessThan(left, right);
                case LEQ -> !greaterThan(left, right);
            };
        return Eval.pure(new BoolValue(result));
    }

    public static Eval<BoolValue> compare(BoolValue left, ComparisonOperation op, BoolValue right) {
        return switch (op) {
            case EQ -> Eval.pure(new BoolValue(left.value() == right.value()));
            case NEQ -> Eval.pure(new BoolValue(left.value() != right.value()));
            default -> Eval.raiseError(new EvalError.ArithmeticError(
                "Comparison " + op + " is not defined for boolean values"));
        };
    }

    private static boolean equals(NumericValue left, NumericValue right) {
        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> l.number() == r.number();
                case RealValue r -> l.number() == r.number();
            };
            case RealValue l -> switch (right) {
                case IntValue r -> l.number() == r.number();
                case RealValue r -> l.number() == r.number();
            };
        };
    }

    private static boolean greaterThan(NumericValue left, NumericValue right) {
        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> l.number() > r.number();
                case RealValue r -> l.number() > r.number();
            };
            case RealValue l -> switch (right) {
                case IntValue r -> l.number() > r.number();
                case RealValue r -> l.number() > r.number();
            };
        };
    }

    private static boolean lessThan(NumericValue left, NumericValue right) {
        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> l.number() < r.number();
                case RealValue r -> l.number() < r.number();
            };
            case RealValue l -> switch (right) {
                case IntValue r -> l.number() < r.number();
                case RealValue r -> l.number() < r.number();
            };
        };
    }
}
