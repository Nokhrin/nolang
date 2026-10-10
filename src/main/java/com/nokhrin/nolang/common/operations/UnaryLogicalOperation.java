package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.values.Value;

public enum UnaryLogicalOperation implements UnaryOperation<Value.BoolValue> {
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
    public Eval<Value.BoolValue> apply(Value.BoolValue operand) {
        return switch (this) {
            case NOT -> Logical.not(operand);
        };
    }
}
