package com.nokhrin.nolang.common.executions;

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
    if (!signature.parameters().isEmpty() && args.size() != signature.parameters().size()) {
      throw new IllegalArgumentException(
          "Function "
              + name()
              + " expected "
              + signature.parameters().size()
              + " parameters, "
              + " got "
              + args.size());
    }
    return implementation.call(args);
  }
}
