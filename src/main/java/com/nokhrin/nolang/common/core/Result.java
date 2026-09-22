package com.nokhrin.nolang.common.core;

public sealed interface Result<A> {
  Environment environment();

  record Success<A>(Environment environment, A value) implements Result<A> {}

  record Failure<A>(Environment environment, EvalError error) implements Result<A> {}

  record Control<A>(Environment environment, ControlSignal signal) implements Result<A> {}
}
