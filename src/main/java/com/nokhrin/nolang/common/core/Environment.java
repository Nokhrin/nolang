package com.nokhrin.nolang.common.core;

import java.util.List;

public record Environment(Scope scope, FunctionRegistry registry, List<String> outputBuffer) {
  public Environment withScope(Scope newScope) {
    return new Environment(newScope, registry, outputBuffer);
  }

  public Environment withRegistry(FunctionRegistry newRegistry) {
    return new Environment(scope, newRegistry, outputBuffer);
  }

  public Environment withOutput(List<String> newOutput) {
    return new Environment(scope, registry, newOutput);
  }
}
