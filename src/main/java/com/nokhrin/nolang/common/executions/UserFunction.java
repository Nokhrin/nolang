package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.functional.*;

import java.util.List;

public record UserFunction(FunctionSignature signature, FunctionBody body, Scope closureScope) implements Function {


    @Override
    public String name() {
        return signature.name();
    }

    @Override
    public Eval<Value> invoke(List<Value> args) {
        List<ParameterSymbol> parameters = signature.parameters();
        if (args.size() != parameters.size()) {
            return Eval.raiseError(new EvalError.ArityError(
                    "Function " + name()
                        + " expected " + parameters.size()
                        + ", got: " + args.size()
                )
            );
        }
        Either<ScopeError, Scope> scopeEither = Either.right(new Scope(closureScope));

        for (int i = 0; i < parameters.size(); i++) {
            final String param = parameters.get(i).name();
            final Value arg = args.get(i);
            scopeEither = scopeEither.flatMap(scope -> scope.define(param, arg));
        }

        return scopeEither.fold(
            scopeError -> Eval.raiseError(new ScopeError.Undefined(
                "Function " + name())),
            functionScope -> catchReturn(body.execute(functionScope))
        );
    }

    private Eval<Value> catchReturn(Eval<Value> bodyEval) {
        return environment -> switch (bodyEval.run(environment)) {
            case Result.Success<Value> success -> success;
            case Result.Failure<Value> failure -> failure;
            case Result.Signal<Value> signal -> switch (signal.signal()) {
                case Return ret -> new Result.Success<>(signal.environment(), ret.value());
                case Break _, Continue _ -> signal;
            };
        };
    }
}
