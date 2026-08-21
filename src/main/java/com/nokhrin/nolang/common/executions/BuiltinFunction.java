package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

import java.util.List;

public record BuiltinFunction(FunctionSignature signature, Callable implementation, String helpText)
    implements Function {
    @Override
    public String name() {
        return signature.name();
    }

    @Override
    public Result invoke(List<Value> args) {
        if (args.size() != signature.parameters().size()) {
            throw new IllegalArgumentException(
                "Function " + name()
                    + " expected " + signature.parameters().size() + " parameters, "
                    + " got " + args.size());
        }

        for (int i = 0; i < args.size(); i++) {
            ParameterSymbol parameter = signature.parameters().get(i);
            Value arg = args.get(i);
            if (!(arg instanceof NumericValue)) {
                throw new IllegalArgumentException("Expected numeric argument for parameter: " + parameter.name());
            }
        }
        return implementation.call(args);
    }
}
