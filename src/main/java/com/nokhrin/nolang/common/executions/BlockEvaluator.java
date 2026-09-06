package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.DynamicTypedParser;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.functional.Eval;

public interface BlockEvaluator {
  Eval<Value> evaluate(DynamicTypedParser.BlockContext block, Scope functionScope);
}
