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
    private final Map<Symbol, Value> values = new HashMap<>();

    public Scope(Scope enclosing) {
        this.enclosing = enclosing;
    }

    public Scope enclosing() {
        return enclosing;
    }

    public void declare(Symbol symbol) {
        symbols.put(symbol.name(), symbol);
    }

    public void assign(Symbol symbol, Value value) {
        values.put(symbol, value);
    }

    public void define(Symbol symbol, Value value) {
        declare(symbol);
        assign(symbol,value);
    }

    public Optional<Symbol> resolve(String name) {
        for (Scope scope = this; scope != null; scope = scope.enclosing) {
            Symbol symbol = symbols.get(name);
            if (symbol != null) {
                return Optional.of(symbol);
            }
        }
        return Optional.empty();
    }

    public Value fetch(VariableSymbol symbol) {
        for (Scope scope = this; scope != null; scope = scope.enclosing) {
            Value value = values.get(symbol);
            if (value != null) {
                return value;
            }
        }
        throw new IllegalStateException("Variable " + symbol.name() + " is used before initialization");
    }
}
