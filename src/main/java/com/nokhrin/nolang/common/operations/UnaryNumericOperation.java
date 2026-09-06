package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.functional.Eval;

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

    public static UnaryNumericOperation fromSymbol(String operator) {
        return switch (operator) {
            case "+" -> PLUS;
            case "-" -> MINUS;
            case "abs" -> ABSOLUTE;
            case "!" -> FACTORIAL;
            case "%" -> PERCENT;
            default -> throw new IllegalArgumentException("Unknown unary operator: " + operator);
        };
    }
}
