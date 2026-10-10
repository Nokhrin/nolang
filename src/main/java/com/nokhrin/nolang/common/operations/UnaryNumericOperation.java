package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.values.NumericValue;

public enum UnaryNumericOperation implements UnaryOperation<NumericValue> {
    PLUS("+"),
    MINUS("-"),
    ABSOLUTE("abs"),
    FACTORIAL("!"),
    PERCENT("%");

    private final String operator;

    UnaryNumericOperation(String operator) {
        this.operator = operator;
    }

    @Override
    public String operator() {
        return operator;
    }

    @Override
    public Eval<NumericValue> apply(NumericValue number) {
        return switch (this) {
            case PLUS -> Numeric.pos(number);
            case MINUS -> Numeric.neg(number);
            case ABSOLUTE -> Numeric.abs(number);
            case FACTORIAL -> Numeric.fact(number);
            case PERCENT -> Numeric.percent(number);
        };
    }

    public static Eval<NumericValue> applySymbol(String symbol, Eval<NumericValue> operand) {
        return fromSymbol(symbol).fold(Eval::raiseError, operation -> operation.apply(operand));
    }

    public static Either<EvalError, UnaryNumericOperation> fromSymbol(String operator) {
        return switch (operator) {
            case "+" -> Either.right(PLUS);
            case "-" -> Either.right(MINUS);
            case "abs" -> Either.right(ABSOLUTE);
            case "!" -> Either.right(FACTORIAL);
            case "%" -> Either.right(PERCENT);
            default ->
                    Either.left(new EvalError.SyntaxError("Unknown unary operator: " + operator));
        };
    }
}
