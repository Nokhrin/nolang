package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Eval;

public interface UnaryOperation<T> {
    String operator();

    Eval<T> apply(T operand);

    default Eval<T>apply(Eval<T>operand){
        return operand.flatMap(this::apply);
    }
}
