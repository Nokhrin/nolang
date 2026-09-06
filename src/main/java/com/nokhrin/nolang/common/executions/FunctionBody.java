package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.functional.Eval;

public interface FunctionBody {
  Eval<Value> execute(Scope functionScope);
}
