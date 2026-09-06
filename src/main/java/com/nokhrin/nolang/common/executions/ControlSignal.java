package com.nokhrin.nolang.common.executions;

public sealed interface ControlSignal permits Break, Continue, Return {
}
