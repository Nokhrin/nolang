package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.combinators.EnvironmentCombinators;
import com.nokhrin.nolang.common.values.Value;

import java.util.List;

public sealed interface Function permits Function.BuiltIn, Function.UserDefined {
    Eval<Value> call(List<Value> args);

    FunctionSignature signature();

    default String helpText() {
        return "";
    }

    record BuiltIn(FunctionSignature signature, FunctionBody body, String helpText)
        implements Function {

        public BuiltIn(FunctionSignature signature, FunctionBody body) {
            this(signature, body, "");
        }

        @Override
        public Eval<Value> call(List<Value> args) {
            return validate(args)
                .fold(
                    Eval::raiseError,
                    _ ->
                        EnvironmentCombinators.getEnvironment()
                            .flatMap(env -> body.execute(env.scope(), args)));
        }

        private Either<EvalError, Unit> validate(List<Value> args) {
            if (!signature.arity().contains(args.size())) {
                return Either.left(
                    new EvalError.ArityError(
                        "Function "
                            + signature.name()
                            + " expected arity "
                            + signature.arity().min()
                            + ".."
                            + signature.arity().max()
                            + ", got: "
                            + args.size()));
            }
            for (int i = 0; i < signature.parameters().size() && i < args.size(); i++) {
                Declaration.Parameter parameter = signature.parameters().get(i);
                Value arg = args.get(i);
                if (!parameter.accepts(arg)) {
                    return Either.left(
                        new EvalError.TypeError(
                            "Function "
                                + signature.name()
                                + " expected type "
                                + parameter.type()
                                + " for parameter "
                                + parameter.name()
                                + ", got: "
                                + arg.getClass().getSimpleName()));
                }
            }
            return Either.right(Unit.INSTANCE);
        }
    }

    record UserDefined(FunctionSignature signature, FunctionBody body, Scope closureScope)
        implements Function {

        @Override
        public Eval<Value> call(List<Value> args) {
            List<Declaration.Parameter> parameters = signature.parameters();
            if (signature.arity().contains(args.size())) {
                return Eval.raiseError(
                    new EvalError.ArityError(
                        "Function "
                            + signature.name()
                            + " expected arity "
                            + signature.arity().min()
                            + ".."
                            + signature.arity().max()
                            + ", got: "
                            + args.size()));
            }
            Either<ScopeError, Scope> scopeEither = Either.right(new Scope(closureScope));

            for (int i = 0; i < parameters.size(); i++) {
                final String param = parameters.get(i).name();
                final Value arg = args.get(i);
                scopeEither = scopeEither.flatMap(scope -> scope.define(param, arg));
            }

            return scopeEither.fold(
                Eval::raiseError, functionScope -> catchReturn(body.execute(functionScope, args)));
        }

        private Eval<Value> catchReturn(Eval<Value> bodyEval) {
            return environment ->
                switch (bodyEval.run(environment)) {
                    case Result.Success<Value> success -> success;
                    case Result.Failure<Value> failure -> failure;
                    case Result.Control<Value> control -> switch (control.signal()) {
                        case ControlSignal.Return ret -> new Result.Success<>(control.environment(), ret.value());
                        case ControlSignal.Break _, ControlSignal.Continue _ -> control;
                    };
                };
        }
    }
}
