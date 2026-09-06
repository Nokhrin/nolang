package com.nokhrin.nolang.functional;

import com.nokhrin.nolang.common.executions.ControlSignal;

public sealed interface Result<A> {
  Environment environment();

  record Success<A>(Environment environment, A value) implements Result<A> {}

  record Failure<A>(Environment environment, EvalError error) implements Result<A> {}

  record Signal<A>(Environment environment, ControlSignal signal) implements Result<A> {}
}
