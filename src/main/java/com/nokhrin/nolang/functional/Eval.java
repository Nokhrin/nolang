package com.nokhrin.nolang.functional;

import com.nokhrin.nolang.common.executions.ControlSignal;
import com.nokhrin.nolang.functional.Result.Failure;
import com.nokhrin.nolang.functional.Result.Signal;
import com.nokhrin.nolang.functional.Result.Success;

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
        return environment -> new Signal<>(environment, signal);
    }

    default <B> Eval<B> flatMap(Function<A, Eval<B>> function) {
        return environment -> {
            Result<A> current = this.run(environment);
            return switch (current) {
                case Success<A> success -> function.apply(success.value()).run(success.environment());
                case Failure<A> failure -> new Failure<>(failure.environment(), failure.error());
                case Signal<A> signal -> new Signal<>(signal.environment(), signal.signal());
            };
        };
    }

    default <B> Eval<B> map(Function<A, B> function) {
        return flatMap(value -> Eval.pure(function.apply(value)));
    }

}
