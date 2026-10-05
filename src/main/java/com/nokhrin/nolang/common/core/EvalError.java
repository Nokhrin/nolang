package com.nokhrin.nolang.common.core;

public sealed interface EvalError
    permits EvalError.ArithmeticError,
    EvalError.ArityError,
    EvalError.SyntaxError,
    EvalError.TypeError,
    ScopeError {
    String message();

    record SyntaxError(String message) implements EvalError {
    }

    record ArithmeticError(String message) implements EvalError {
    }

    record TypeError(String message) implements EvalError {
    }

    record ArityError(String message) implements EvalError {
    }
}
