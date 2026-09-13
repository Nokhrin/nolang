package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.values.Value;

import java.util.List;

public interface FunctionBody {
    Eval<Value> execute(Scope functionScope, List<Value> args);
}
