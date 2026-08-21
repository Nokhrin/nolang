package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;

import java.util.function.UnaryOperator;

public enum PrefixOperation implements UnaryOperator<NumericValue> {
    PLUS("+"),
    MINUS("-");

    private final String operator;

    PrefixOperation(String operator) {
        this.operator = operator;
    }

    public static PrefixOperation fromSymbol(String operator) {
        return switch (operator) {
            case "+" -> PLUS;
            case "-" -> MINUS;
            default -> throw new IllegalArgumentException("Unknown unary operator: " + operator);
        };
    }

    @Override
    public NumericValue apply(NumericValue number) {
        return switch (this) {
            case PLUS -> number;
            case MINUS -> Arithmetic.neg(number);
        };
    }

}
