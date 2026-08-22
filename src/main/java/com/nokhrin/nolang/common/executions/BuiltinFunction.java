package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.RealValue;
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
          "Function "
              + name()
              + " expected "
              + signature.parameters().size()
              + " parameters, "
              + " got "
              + args.size());
    }

    for (int i = 0; i < args.size(); i++) {
      ParameterSymbol parameter = signature.parameters().get(i);
      Value arg = args.get(i);
      Type expectedType = parameter.type();
      boolean isValid =
          switch (expectedType) {
            case INTEGER -> arg instanceof IntValue;
            case REAL -> arg instanceof RealValue;
            case BOOLEAN -> arg instanceof BoolValue;
            case VOID -> false;
          };

      if (!isValid) {
        throw new IllegalArgumentException(
            "Function "
                + name()
                + " expected for parameter: "
                + parameter.name()
                + ", got: "
                + arg.getClass().getSimpleName());
      }
    }
    return implementation.call(args);
  }
}
