package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.values.Value;

public class Logical {

    public static Eval<Value.BoolValue> or(Value.BoolValue left, Value.BoolValue right) {
        return Eval.pure(new Value.BoolValue(left.value() || right.value()));
    }

    public static Eval<Value.BoolValue> and(Value.BoolValue left, Value.BoolValue right) {
        return Eval.pure(new Value.BoolValue(left.value() && right.value()));
    }

    public static Eval<Value.BoolValue> not(Value.BoolValue operand) {
        return Eval.pure(new Value.BoolValue(!operand.value()));
    }
}
