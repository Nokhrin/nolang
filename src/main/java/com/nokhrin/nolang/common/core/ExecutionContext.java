package com.nokhrin.nolang.common.core;

import java.util.List;

public record ExecutionContext(Scope scope, FunctionRegistry registry, List<String> outputBuffer) {
    public ExecutionContext withScope(Scope newScope) {
        return new ExecutionContext(newScope, registry, outputBuffer);
  }

    public ExecutionContext withRegistry(FunctionRegistry newRegistry) {
        return new ExecutionContext(scope, newRegistry, outputBuffer);
  }

    public ExecutionContext withOutput(List<String> newOutput) {
        return new ExecutionContext(scope, registry, newOutput);
  }
}
