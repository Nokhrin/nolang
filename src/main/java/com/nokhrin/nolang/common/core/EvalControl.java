package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.values.Value;

public sealed interface EvalControl
        permits EvalControl.Break, EvalControl.Continue, EvalControl.Return {
    String message();

    record Break() implements EvalControl {

        @Override
        public String message() {
            return "break outside loop";
        }
    }

    record Continue() implements EvalControl {
        @Override
        public String message() {
            return "continue outside loop";
        }
    }

    record Return(Value value) implements EvalControl {
        @Override
        public String message() {
            return "return outside function";
        }
    }
}
