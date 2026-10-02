package com.nokhrin.nolang.common.combinators;

import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.Scope;
import com.nokhrin.nolang.common.core.ScopeError;
import com.nokhrin.nolang.common.values.Value;
import java.util.function.Function;

public class ScopeCombinators {

  public static Eval<Value> assignVariable(String name, Eval<Value> valueEval) {
    return valueEval.flatMap(value -> modifyScope(scope -> scope.assignOrDefine(name, value)));
  }

  public static Eval<Value> modifyScope(Function<Scope, Either<ScopeError, Scope>> operation) {
    return ContextCombinators.getContext()
        .flatMap(
            environment -> {
              Either<ScopeError, Scope> result = operation.apply(environment.scope());
              return result.fold(
                  scopeError -> Eval.raiseError(scopeError),
                  updatedScope ->
                      ContextCombinators.updateContext(
                              envModified -> envModified.withScope(updatedScope))
                          .flatMap(_ -> Eval.pure(Value.VoidValue.INSTANCE)));
            });
  }
}
