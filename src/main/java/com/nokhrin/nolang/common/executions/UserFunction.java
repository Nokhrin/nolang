package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.values.Value;

import java.util.List;

public record UserFunction(FunctionSignature signature, FunctionBody body, Scope functionScope)
    implements Function {
    @Override
    public String name() {
        return signature.name();
    }

    @Override
    public Result invoke(List<Value> actualParameters) {
        List<ParameterSymbol> formalParameters = signature.parameters();

        if (actualParameters.size() != formalParameters.size()) {
            throw new IllegalArgumentException(
                signature.name()
                    + " expected: "
                    + formalParameters.size()
                    + " parameters,"
                    + " got "
                    + actualParameters.size());
        }
        Scope functionScopeFrame = new Scope(functionScope());
        for (int i = 0; i < formalParameters.size(); i++) {
            functionScopeFrame.declare(formalParameters.get(i).name(), actualParameters.get(i));
        }

        return body.execute(functionScopeFrame);
    }
}
