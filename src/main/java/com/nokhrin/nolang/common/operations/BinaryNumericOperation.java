package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;

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

    public static Either<EvalError, BinaryNumericOperation> fromSymbol(String operator) {
        return switch (operator) {
            case "+" -> Either.right(ADD);
            case "-" -> Either.right(SUB);
            case "*" -> Either.right(MUL);
            case "/" -> Either.right(DIV);
            case "^" -> Either.right(POW);
            default -> Either.left(new EvalError.SyntaxError("Unknown binary operator: " + operator));
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
