package com.nokhrin.nolang.common.core;

public sealed interface InterruptReason permits InterruptReason.Error, InterruptReason.Control {
    String message();

    record Error(EvalError cause) implements InterruptReason {
        @Override
        public String message() {
            return cause.message();
        }
    }

    record Control(EvalControl cause) implements InterruptReason {
        @Override
        public String message() {
            return cause.message();
        }
    }
}
