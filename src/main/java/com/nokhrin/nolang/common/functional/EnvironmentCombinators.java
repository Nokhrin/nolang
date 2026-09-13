package com.nokhrin.nolang.common.functional;

import com.nokhrin.nolang.common.core.Environment;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.Result;

import java.util.function.Function;

public class EnvironmentCombinators {
    public static Eval<Environment> getEnvironment() {
        return environment -> new Result.Success<>(environment, environment);
    }

    public static Eval<Unit> modifyEnvironment(Function<Environment, Environment> function) {
        return environment -> new Result.Success<>(function.apply(environment), Unit.INSTANCE);
    }
}
