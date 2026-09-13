package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.core.Eval;

public enum UnaryLogicalOperation implements UnaryOperation<Value.Bool> {
    NOT("NOT");

    private final String operator;

    UnaryLogicalOperation(String operator) {
        this.operator = operator;
    }

    @Override
    public String operator() {
        return operator;
    }

    @Override
    public Eval<Value.Bool> apply(Value.Bool operand) {
        return switch (this) {
            case NOT -> Logical.not(operand);
        };

    }
}
