package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.values.Value;

import java.util.List;

public record Function(FunctionSignature signature, FunctionBody body, Scope scope, String help) {
    public Function(FunctionSignature signature, FunctionBody body, Scope closureScope) {
        this(signature, body, closureScope, "");
    }

    public Function(FunctionSignature signature, FunctionBody body, String helpText) {
        this(signature, body, new Scope(), helpText);
    }

    public Eval<Value> call(List<Value> args) {
        return validate(args)
            .fold(
                Eval::raiseError,
                _ -> executionContext -> body.execute(executionContext, args).run(executionContext));
    }

    private Either<EvalError, Scope> bindParameters(List<Value> args) {
        Either<EvalError, Scope> scopeEither = Either.right(new Scope(scope));
        for (int i = 0; i < signature.parameters().size(); i++) {
            Declaration.Parameter parameter = signature.parameters().get(i);
            Value arg = args.get(i);
            scopeEither =
                scopeEither.flatMap(
                    scope -> {
                        Either<ScopeError, Scope> defined = scope.define(parameter.name(), arg);
                        return defined.fold(Either::<EvalError, Scope>left, Either::right);
                    });
        }
        return scopeEither;
    }

    private Eval<Value> handleReturn(Eval<Value> bodyEval) {
        return executionContext ->
            switch (bodyEval.run(executionContext)) {
                case EvalResult.Returned<Value> result -> result;
                case EvalResult.Interrupted<Value> interrupted -> switch (interrupted.reason()) {
                    case InterruptReason.Error _ -> interrupted;
                    case InterruptReason.Control control -> switch (control.cause()) {
                        case EvalControl.Return ret ->
                            new EvalResult.Returned<>(interrupted.executionContext(), ret.value());
                        case EvalControl.Break _, EvalControl.Continue _ -> interrupted;
                    };
                };
            };
    }

    private Either<EvalError, Unit> validate(List<Value> args) {
        if (!signature.arity().contains(args.size())) {
            return Either.left(
                new EvalError.ArityError(
                    "Function %s expected arity %d..%d, got: %d"
                        .formatted(
                            signature.name(),
                            signature.arity().min(),
                            signature.arity().max(),
                            args.size())));
        }
        for (int i = 0; i < signature.parameters().size() && i < args.size(); i++) {
            Declaration.Parameter parameter = signature.parameters().get(i);
            Value arg = args.get(i);
            if (!parameter.accepts(arg)) {
                return Either.left(
                    new EvalError.TypeError(
                        "Function %s expected type %s for parameter %s, got: %s"
                            .formatted(
                                signature.name(),
                                parameter.type(),
                                parameter.name(),
                                arg.getClass().getSimpleName())));
            }
        }
        return Either.right(Unit.INSTANCE);
    }
}
