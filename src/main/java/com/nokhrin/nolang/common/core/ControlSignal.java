package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.values.Value;

public sealed interface ControlSignal
    permits ControlSignal.Break, ControlSignal.Continue, ControlSignal.Return {

  record Break() implements ControlSignal {}

  record Continue() implements ControlSignal {}

  record Return(Value value) implements ControlSignal {}
}
