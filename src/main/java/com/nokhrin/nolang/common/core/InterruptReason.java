package com.nokhrin.nolang.common.core;

public sealed interface InterruptReason permits InterruptReason.Error, InterruptReason.Control {
    String message();

    record Error(EvalError error) implements InterruptReason {
        @Override
        public String message() {
            return error.message();
        }
    }

    record Control(EvalControl signal) implements InterruptReason {
        @Override
        public String message() {
            return signal.message();
        }
    }

}
