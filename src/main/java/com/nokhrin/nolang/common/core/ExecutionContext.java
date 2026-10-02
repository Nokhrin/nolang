package com.nokhrin.nolang.common.core;

import java.util.List;

public record ExecutionContext(Scope scope, FunctionRegistry registry, List<String> stdout) {
  public ExecutionContext withScope(Scope newScope) {
    return new ExecutionContext(newScope, registry, stdout);
  }

  public ExecutionContext withRegistry(FunctionRegistry newRegistry) {
    return new ExecutionContext(scope, newRegistry, stdout);
  }

  public ExecutionContext withOutput(List<String> newOutput) {
    return new ExecutionContext(scope, registry, newOutput);
  }
}
