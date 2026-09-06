package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.values.*;
import com.nokhrin.nolang.functional.Eval;

import java.util.List;

public record BuiltinFunction(FunctionSignature signature, Callable implementation, String helpText)
    implements Function {
    @Override
    public String name() {
        return signature.name();
    }

    @Override
    public Eval<Value> invoke(List<Value> args) {
        if (args.size() != signature.parameters().size()) {
            throw new IllegalArgumentException(
                "Function " + name()
                    + " expected " + signature.parameters().size() + " parameters, "
                    + " got: " + args.size());
        }

        for (int i = 0; i < args.size(); i++) {
            ParameterSymbol parameter = signature.parameters().get(i);
            Value arg = args.get(i);
            Type expectedType = parameter.type();

            boolean typeMatched = switch (expectedType) {
                case Type.IntType _ -> arg instanceof IntValue;
                case Type.RealType _ -> arg instanceof RealValue;
                case Type.BoolType _ -> arg instanceof BoolValue;
                case Type.VoidType _ -> arg instanceof VoidValue;
            };

            if (!typeMatched) {
                throw new IllegalArgumentException(
                    "Function " + name()
                        + " expected type " + expectedType
                        + " for parameter " + parameter.name()
                        + ", got: " + arg.getClass().getSimpleName());
            }
        }
        return implementation.call(args);
    }
}
