package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.core.Eval;

public enum BinaryLogicalOperation implements BinaryOperation<Value.Bool> {
    AND("AND"),
    OR("OR");

    private final String operator;

    BinaryLogicalOperation(String operator) {
        this.operator = operator;
    }

    @Override
    public String operator() {
        return operator;
    }


    @Override
    public Eval<Value.Bool> apply(Value.Bool left, Value.Bool right) {
        return switch (this) {
            case AND -> Logical.and(left, right);
            case OR -> Logical.or(left, right);
        };
    }

}
