package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.core.Result.Control;
import com.nokhrin.nolang.common.core.Result.Failure;
import com.nokhrin.nolang.common.core.Result.Success;
import java.util.function.Function;

public interface Eval<A> {

  Result<A> run(Environment environment);

  static <A> Eval<A> pure(A value) {
    return environment -> new Success<>(environment, value);
  }

  static <A> Eval<A> raiseError(EvalError error) {
    return environment -> new Failure<>(environment, error);
  }

  static <A> Eval<A> raiseSignal(ControlSignal signal) {
    return environment -> new Result.Control<>(environment, signal);
  }

  default <B> Eval<B> flatMap(Function<A, Eval<B>> function) {
    return environment -> {
      Result<A> current = this.run(environment);
      return switch (current) {
        case Success<A> success -> function.apply(success.value()).run(success.environment());
        case Failure<A> failure -> new Failure<>(failure.environment(), failure.error());
        case Result.Control<A> control -> new Control<>(control.environment(), control.signal());
      };
    };
  }

  default <B> Eval<B> map(Function<A, B> function) {
    return environment -> {
      Result<A> result = this.run(environment);
      return switch (result) {
        case Success<A> success ->
            new Success<>(success.environment(), function.apply(success.value()));
        case Failure<A> failure -> new Failure<>(failure.environment(), failure.error());
        case Result.Control<A> control ->
            new Result.Control<>(control.environment(), control.signal());
      };
    };
  }

  default <B> Eval<B> widen() {
    return (Eval<B>) this;
  }
}
