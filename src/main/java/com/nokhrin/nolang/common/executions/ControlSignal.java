package com.nokhrin.nolang.common.executions;

public sealed interface ControlSignal extends Result permits Break, Continue, Return {
}
