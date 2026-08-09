package com.nokhrin.nolang.common.executions;

public sealed interface ControlSignal {
    record Normal(Result value) implements ControlSignal {}
    record Break() implements ControlSignal {}
    record Continue() implements ControlSignal {}
    record Return(Result value) implements ControlSignal {}
}
