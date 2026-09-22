package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.values.Value;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record FunctionRegistry(Map<String, Function> functions) {
  public FunctionRegistry {
    functions = Map.copyOf(functions);
  }

  public boolean isBuiltin(String funcName) {
    return functions.containsKey(funcName);
  }

  public Optional<Function> fetch(String funcName) {
    return Optional.ofNullable(functions.get(funcName));
  }

  public Eval<Value> evaluate(String funcName, List<Value> args) {
    return this.fetch(funcName)
        .map(function -> function.call(args))
        .orElseGet(() -> Eval.raiseError(new ScopeError.UndefinedFunction(funcName)));
  }

  public FunctionRegistry define(FunctionSignature signature, FunctionBody body, Scope scope) {
    Map<String, Function> childFunctions = new HashMap<>(functions);
    childFunctions.put(signature.name(), new Function.UserDefined(signature, body, scope));
    return new FunctionRegistry(Map.copyOf(childFunctions));
  }

  public String getFuncHelp(String funcName) {
    return fetch(funcName)
        .map(
            function ->
                function.helpText().isEmpty()
                    ? "No help available for: " + function.signature().name()
                    : function.helpText())
        .orElse("Not registered function: " + funcName);
  }

  public String getRegistryHelp() {
    String registeredFunctions = String.join("\n", functions.keySet());
    return "Built-in functions:\n"
        + registeredFunctions
        + "\n"
        + "Use /h <function name> for details. Example: /h sin";
  }
}
