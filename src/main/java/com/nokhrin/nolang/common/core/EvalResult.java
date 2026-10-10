package com.nokhrin.nolang.common.core;

public sealed interface EvalResult<A> {
    ExecutionContext executionContext();

    record Returned<A>(ExecutionContext executionContext, A value) implements EvalResult<A> {}

    record Interrupted<A>(ExecutionContext executionContext, InterruptReason reason)
            implements EvalResult<A> {}
}
