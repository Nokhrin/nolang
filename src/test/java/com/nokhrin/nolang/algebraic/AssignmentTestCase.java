package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.values.Value;

public record AssignmentTestCase(
        String id, String varName, String expressionToAssign, Value expected) {
    @Override
    public String toString() {
        return id;
    }
}
