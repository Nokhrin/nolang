package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.definitions.FunctionParameter;
import com.nokhrin.nolang.common.functional.EnvironmentCombinators;
import com.nokhrin.nolang.common.functional.Unit;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

import java.util.List;

public sealed interface Function permits Function.BuiltIn, Function.UserDefined {
    Eval<Value> call(List<Value> args);

    record BuiltIn(FunctionSignature signature, FunctionBody body, String helpText)
        implements Function {

        @Override
        public Eval<Value> call(List<Value> args) {
            return validate(args).fold(
                Eval::raiseError,
                _ -> EnvironmentCombinators.getEnvironment()
                    .flatMap(env -> body.execute(env.scope(), args))
            );
        }

        private Either<EvalError, Unit> validate(List<Value> args) {
            if (!signature.arity().contains(args.size())) {
                return Either.left(new EvalError.ArityError(
                    "Function " + signature.name()
                        + " expected arity " + signature.arity().min()
                        + ".." + signature.arity().max().map(String::valueOf).orElse("*")
                        + ", got: " + args.size()));
            }
            for (int i = 0; i < signature.parameters().size() && i < args.size(); i++) {
                FunctionParameter parameter = signature.parameters().get(i);
                Value arg = args.get(i);
                if (!typeMatches(parameter.type(), arg)) {
                    return Either.left(new EvalError.TypeError(
                        "Function " + signature.name()
                            + " expected type " + parameter.type()
                            + " for parameter " + parameter.name()
                            + ", got: " + args.getClass().getSimpleName()));
                }
            }
            return Either.right(Unit.INSTANCE);
        }

        private boolean typeMatches(Type expected, Value arg) {
            return switch (expected) {
                case Type.Int _ -> arg instanceof NumericValue.Int;
                case Type.Real _ -> arg instanceof NumericValue.Real;
                case Type.Bool _ -> arg instanceof Value.Bool;
                case Type.Void _ -> arg instanceof Value.Void;
            };
        }
    }

    record UserDefined(FunctionSignature signature, FunctionBody body, Scope closureScope) implements Function {

        @Override
        public Eval<Value> call(List<Value> args) {
            List<FunctionParameter> parameters = signature.parameters();
            if (args.size() != parameters.size()) {
                return Eval.raiseError(new EvalError.ArityError(
                        "Function " + signature.name()
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
                scopeError -> Eval.raiseError(new ScopeError.UndefinedFunction(signature.name())),
                functionScope -> catchReturn(body.execute(functionScope, args))
            );
        }

        private Eval<Value> catchReturn(Eval<Value> bodyEval) {
            return environment -> switch (bodyEval.run(environment)) {
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
