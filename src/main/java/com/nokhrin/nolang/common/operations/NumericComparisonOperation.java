package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

public enum NumericComparisonOperation implements RelationOperation<NumericValue, NumericValue> {
    GT(">"),
    LT("<"),
    EQ("=="),
    NEQ("!="),
    GEQ(">="),
    LEQ("<=");

    private final String operator;

    NumericComparisonOperation(String operator) {
        this.operator = operator;
    }

    public static Either<EvalError, NumericComparisonOperation> fromSymbol(String symbol) {
        return switch (symbol) {
            case ">" -> Either.right(GT);
            case "<" -> Either.right(LT);
            case "==" -> Either.right(EQ);
            case "!=" -> Either.right(NEQ);
            case ">=" -> Either.right(GEQ);
            case "<=" -> Either.right(LEQ);
            default -> Either.left(new EvalError.SyntaxError("Unknown comparison operator"));
        };
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

    @Override
    public String operator() {
        return operator;
    }

    @Override
    public Eval<Value.BoolValue> apply(NumericValue left, NumericValue right) {
        return compare(left, this, right);
    }
}
