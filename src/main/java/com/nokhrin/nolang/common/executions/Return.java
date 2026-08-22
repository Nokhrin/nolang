package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.values.Value;

public record Return(Value value) implements ControlSignal {}
