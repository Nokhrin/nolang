package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.values.Value;

public enum BooleanComparisonOperation
        implements RelationOperation<Value.BoolValue, Value.BoolValue> {
    EQ("=="),
    NEQ("!=");

    private final String operator;

    BooleanComparisonOperation(String operator) {
        this.operator = operator;
    }

    public static Either<EvalError, BooleanComparisonOperation> fromSymbol(String symbol) {
        return switch (symbol) {
            case "==" -> Either.right(EQ);
            case "!=" -> Either.right(NEQ);
            default -> Either.left(new EvalError.SyntaxError("Unknown comparison operator"));
        };
    }

    public static Eval<Value.BoolValue> compare(
            Value.BoolValue left, BooleanComparisonOperation op, Value.BoolValue right) {
        return switch (op) {
            case EQ -> Eval.pure(new Value.BoolValue(left.value() == right.value()));
            case NEQ -> Eval.pure(new Value.BoolValue(left.value() != right.value()));
        };
    }

    @Override
    public String operator() {
        return operator;
    }

    @Override
    public Eval<Value.BoolValue> apply(Value.BoolValue left, Value.BoolValue right) {
        return compare(left, this, right);
    }
}
