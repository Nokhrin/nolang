package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;

public enum ComparisonOperation implements Relation<NumericValue, NumericValue> {
    GT(">"),
    LT("<"),
    EQ("=="),
    NEQ("!="),
    GEQ(">="),
    LEQ("<=");

    private final String operator;

    ComparisonOperation(String operator) {
        this.operator = operator;
    }

    public static Either<EvalError, ComparisonOperation> fromSymbol(String symbol) {
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

    @Override
    public String operator() {
        return operator;
    }

    @Override
    public Eval<Value.Bool> apply(NumericValue left, NumericValue right) {
        return Logical.compare(left, this, right);
    }
}
