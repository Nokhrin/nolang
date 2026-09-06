package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.functional.Eval;

public enum BinaryNumericOperation implements BinaryOperation<NumericValue> {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    POW("^");

    private final String operator;

    BinaryNumericOperation(String operator) {
        this.operator = operator;
    }

    public static BinaryNumericOperation fromSymbol(String operator) {
        return switch (operator) {
            case "+" -> ADD;
            case "-" -> SUB;
            case "*" -> MUL;
            case "/" -> DIV;
            case "^" -> POW;
            default -> throw new IllegalArgumentException("Unknown binary operator: " + operator);
        };
    }

    @Override
    public Eval<NumericValue> apply(NumericValue left, NumericValue right) {
        return switch (this) {
            case ADD -> Numeric.add(left, right);
            case SUB -> Numeric.sub(left, right);
            case MUL -> Numeric.mul(left, right);
            case DIV -> Numeric.div(left, right);
            case POW -> Numeric.pow(left, right);
        };
    }

    @Override
    public String operator() {
        return operator;
    }
}
