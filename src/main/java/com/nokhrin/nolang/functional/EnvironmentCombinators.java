package com.nokhrin.nolang.functional;

import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.values.VoidValue;

import java.util.function.Function;

public class EnvironmentCombinators {
    public static Eval<Value> whileLoop(Eval<Value> condition, Eval<Value> body) {
        return condition.flatMap(conditionValue -> {
            if (conditionValue instanceof BoolValue boolValue) {
                return boolValue.value()
                    ? body.flatMap(_ -> whileLoop(condition, body))
                    : Eval.pure(VoidValue.INSTANCE);
            }
            return Eval.raiseError(new EvalError.TypeError("While condition must evaluate to boolean"));
        });
    }

    public static Eval<Value> assignVariable(String name, Eval<Value> valueEval) {
        return valueEval.flatMap(value -> modifyScope(scope -> scope.assignOrDefine(name, value)));
    }

    public static Eval<Value> modifyScope(Function<Scope, Either<ScopeError, Scope>> operation) {
        return getEnvironment()
            .flatMap(
                environment -> {
                    Either<ScopeError, Scope> result = operation.apply(environment.scope());
                    return result.fold(
                        scopeError -> Eval.raiseError(scopeError),
                        updatedScope -> modifyEnvironment(envModified ->
                            envModified.withScope(updatedScope))
                            .flatMap(_ -> Eval.pure(VoidValue.INSTANCE))
                    );
                });

    }

    public static Eval<Environment> getEnvironment() {
        return environment -> new Result.Success<>(environment, environment);
    }

    public static Eval<Unit> modifyEnvironment(Function<Environment, Environment> function) {
        return environment -> new Result.Success<>(function.apply(environment), Unit.INSTANCE);
    }
}
