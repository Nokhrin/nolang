package com.nokhrin.nolang.common.core;

import java.util.function.Function;

public interface Eval<A> {
    static <A> Eval<A> pure(A value) {
        return executionContext -> new EvalResult.Returned<>(executionContext, value);
    }

    static <A> Eval<A> raiseError(EvalError error) {
        return executionContext ->
            new EvalResult.Interrupted<>(executionContext, new InterruptReason.Error(error));
    }

    static <A> Eval<A> raiseSignal(EvalControl signal) {
        return executionContext ->
            new EvalResult.Interrupted<>(executionContext, new InterruptReason.Control(signal));
    }

    EvalResult<A> run(ExecutionContext executionContext);

    default <B> Eval<B> flatMap(Function<A, Eval<B>> function) {
        return executionContext -> {
            EvalResult<A> current = this.run(executionContext);
            return switch (current) {
                case EvalResult.Returned<A> returned ->
                    function.apply(returned.value()).run(returned.executionContext());
                case EvalResult.Interrupted<A> interrupted ->
                    new EvalResult.Interrupted<>(interrupted.executionContext(), interrupted.reason());
            };
        };
    }

    default <B> Eval<B> map(Function<A, B> function) {
        return executionContext -> {
            EvalResult<A> current = this.run(executionContext);
            return switch (current) {
                case EvalResult.Returned<A> returned -> new EvalResult.Returned<>(
                    returned.executionContext(), function.apply(returned.value())
                );
                case EvalResult.Interrupted<A> interrupted -> new EvalResult.Interrupted<>(
                    interrupted.executionContext(), interrupted.reason()
                );
            };
        };
    }


}

