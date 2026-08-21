package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.values.Value;

import java.util.List;

public interface Callable {
    Result call(List<Value> args);
}
