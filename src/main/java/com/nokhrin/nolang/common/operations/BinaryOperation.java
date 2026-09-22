package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Eval;

public interface BinaryOperation<T> {
  String operator();

  Eval<T> apply(T left, T right);

  default Eval<T> apply(Eval<T> left, Eval<T> right) {
    return left.flatMap(l -> right.flatMap(r -> apply(l, r)));
  }
}
