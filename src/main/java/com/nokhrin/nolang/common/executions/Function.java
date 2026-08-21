package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.values.Value;

import java.util.List;

public sealed interface Function permits BuiltinFunction, UserFunction {
    String name();

    Result invoke(List<Value> args);
}
