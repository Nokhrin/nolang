package com.nokhrin.nolang.functional;

public sealed interface ScopeError extends EvalError
    permits ScopeError.Undefined,
    ScopeError.Duplicated,
    ScopeError.Uninitialized {

    record Undefined(String name) implements com.nokhrin.nolang.functional.ScopeError {
        public String message() {
            return
                "Undefined variable: " + name;
        }
    }

    record Duplicated(String name) implements com.nokhrin.nolang.functional.ScopeError {
        @Override
        public String message() {
            return
                "Duplicated variable: " + name;
        }
    }

    record Uninitialized(String name) implements com.nokhrin.nolang.functional.ScopeError {
        public String message() {
            return
                "Uninitialized variable: " + name;
        }
    }
}
