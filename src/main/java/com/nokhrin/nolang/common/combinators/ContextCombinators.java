package com.nokhrin.nolang.common.combinators;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalResult;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.core.Unit;
import com.nokhrin.nolang.common.values.Value;
import java.util.List;
import java.util.function.Function;

public class ContextCombinators {

    public static Eval<ExecutionContext> getContext() {
        return executionContext -> new EvalResult.Returned<>(executionContext, executionContext);
    }

    public static Eval<Unit> updateContext(Function<ExecutionContext, ExecutionContext> function) {
        return executionContext ->
                new EvalResult.Returned<>(function.apply(executionContext), Unit.INSTANCE);
    }

    public static Eval<Value> callFunction(String funcName, List<Value> funcArgs) {
        return getContext()
                .flatMap(
                        executionContext ->
                                executionContext.registry().evaluate(funcName, funcArgs));
    }
}
