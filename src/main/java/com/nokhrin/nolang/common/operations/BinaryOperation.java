package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.functional.Eval;

import java.util.function.BinaryOperator;

public interface BinaryOperation<T>  {
    String operator();

    Eval<T> apply(T left, T right);
}
