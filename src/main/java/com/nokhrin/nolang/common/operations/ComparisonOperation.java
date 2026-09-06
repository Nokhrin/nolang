package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.functional.Eval;

public enum ComparisonOperation {
    GT(">"),
    LT("<"),
    EQ("=="),
    NEQ("!="),
    GEQ(">="),
    LEQ("<=");

    private final String operator;

    ComparisonOperation(String s) {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ComparisonOperation fromSymbol(String symbol) {
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

    public Eval<BoolValue> apply(NumericValue left, NumericValue right) {
        return Logical.compare(left, this, right);
    }
}
