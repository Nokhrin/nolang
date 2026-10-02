package com.nokhrin.nolang.common.combinators;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.values.Value;

public class EvalCombinators {
  private EvalCombinators() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static Eval<Value> upcastToValue(Eval<? extends Value> eval) {
    return eval.map(value -> value);
  }
}
