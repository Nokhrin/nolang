package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.functional.Eval;

public enum BinaryLogicalOperation implements BinaryOperation<BoolValue> {
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
    public Eval<BoolValue> apply(BoolValue left, BoolValue right) {
        return switch (this) {
            case AND -> Logical.and(left, right);
            case OR -> Logical.or(left, right);
        };
    }

}
