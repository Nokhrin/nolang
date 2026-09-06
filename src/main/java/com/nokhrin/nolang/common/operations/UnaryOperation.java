package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.functional.Eval;

public interface UnaryOperation<T> {
    String operator();

    Eval<T> apply(T operand);
}
