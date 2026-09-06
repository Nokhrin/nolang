package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.functional.Eval;
import java.util.List;

public sealed interface Function permits BuiltinFunction, UserFunction {
  String name();

  Eval<Value> invoke(List<Value> args);
}
