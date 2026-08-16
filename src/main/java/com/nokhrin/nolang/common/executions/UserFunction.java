package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.definitions.VariableSymbol;
import com.nokhrin.nolang.common.values.Value;
import java.util.List;

public record UserFunction(FunctionSignature signature, FunctionBody body, Scope functionScope)
    implements Function {
  @Override
  public String name() {
    return signature.name();
  }

  @Override
  public Result invoke(List<Value> args) {
    List<ParameterSymbol> formalParams = signature.parameters();

    if (args.size() != formalParams.size()) {
      throw new IllegalArgumentException(
          signature.name()
              + " expected: "
              + formalParams.size()
              + " parameters,"
              + " got "
              + args.size());
    }
    Scope functionScopeFrame = new Scope(functionScope());
    for (int i = 0; i < formalParams.size(); i++) {
      ParameterSymbol parameterSymbol = formalParams.get(i);
      VariableSymbol paramVar = new VariableSymbol(parameterSymbol.name(), parameterSymbol.type());
      functionScopeFrame.declare(paramVar);
      functionScopeFrame.assign(paramVar, args.get(i));
    }

    return body.execute(functionScopeFrame);
  }
}
