package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.functional.Eval;
import java.util.List;

public interface Callable {
  Eval<Value> call(List<Value> args);
}
