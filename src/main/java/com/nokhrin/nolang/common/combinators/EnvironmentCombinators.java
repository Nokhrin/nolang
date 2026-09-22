package com.nokhrin.nolang.common.combinators;

import com.nokhrin.nolang.common.core.Environment;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.Result;
import com.nokhrin.nolang.common.core.Unit;
import com.nokhrin.nolang.common.values.Value;

import java.util.List;
import java.util.function.Function;

public class EnvironmentCombinators {
    public static Eval<Value> callFunction(String funcName, List<Value> args) {
        return getEnvironment().flatMap(env -> env.registry().evaluate(funcName, args));
    }

    public static Eval<Environment> getEnvironment() {
        return environment -> new Result.Success<>(environment, environment);
    }

    public static Eval<Unit> modifyEnvironment(Function<Environment, Environment> function) {
        return environment -> new Result.Success<>(function.apply(environment), Unit.INSTANCE);
    }
}
