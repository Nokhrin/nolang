package com.nokhrin.nolang.common.core;

public sealed interface ScopeError extends EvalError
    permits ScopeError.UndefinedVariable,
    ScopeError.DuplicatedVariable,
    ScopeError.UninitializedVariable {

    record UndefinedVariable(String name) implements ScopeError {
        public String message() {
            return
                "Undefined variable: " + name;
        }
    }

    record DuplicatedVariable(String name) implements ScopeError {
        @Override
        public String message() {
            return
                "Duplicated variable: " + name;
        }
    }

    record UninitializedVariable(String name) implements ScopeError {
        public String message() {
            return
                "Uninitialized variable: " + name;
        }
    }

    record UndefinedFunction(String name) implements EvalError {
        public String message() {
            return "Undefined function: " + name;
        }
    }
}
