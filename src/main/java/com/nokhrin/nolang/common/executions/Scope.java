package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.definitions.Symbol;
import com.nokhrin.nolang.common.definitions.VariableSymbol;
import com.nokhrin.nolang.common.values.Value;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Scope {
  private final Scope enclosing;
  private final Map<String, Symbol> symbols = new HashMap<>();
  private final Map<String, Optional<Value>> values = new HashMap<>();

  public Scope(Scope enclosing) {
    this.enclosing = enclosing;
  }

  public Scope enclosing() {
    return enclosing;
  }

  public void declare(Symbol symbol) {
    symbols.put(symbol.name(), symbol);

    if (symbol instanceof VariableSymbol) {
      values.putIfAbsent(symbol.name(), Optional.empty());
    }
  }

  public void assign(Symbol symbol, Value value) {
    for (Scope scope = this; scope != null; scope = scope.enclosing) {
      if (scope.values.containsKey(symbol.name())) {
        scope.values.putIfAbsent(symbol.name(), Optional.of(value));
        return;
      }
    }
    values.put(symbol.name(), Optional.of(value));
  }

  public void define(Symbol symbol, Value value) {
    declare(symbol);
    assign(symbol, value);
  }

  public Optional<Symbol> resolve(String name) {
    return Optional.ofNullable(symbols.get(name))
        .or(() -> enclosing == null ? Optional.empty() : enclosing.resolve(name));
  }

  public <T extends Symbol> Optional<T> resolve(String name, Class<T> type) {
    return resolve(name).filter(type::isInstance).map(type::cast);
  }

  public Value fetchVariable(String variableName) {
    return lookupVariable(variableName)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Variable " + variableName + " is used before initialization"));
  }

  private Optional<Value> lookupVariable(String name) {
    for (Scope scope = this; scope != null; scope = scope.enclosing) {
      if (scope.values.containsKey(name)) {
        return scope.values.get(name);
      }
    }
    return Optional.empty();
  }
}
