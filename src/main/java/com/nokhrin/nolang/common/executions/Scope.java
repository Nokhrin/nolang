package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.DynamicTypedParser;
import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.values.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scope {
    private final Scope parent;
    private final Map<String, Value> bindings;

    /**
     * Local
     *
     * @param parent
     */
    public Scope(Scope parent) {
        this.parent = parent;
        this.bindings = new HashMap<>();
    }

    /**
     * Global
     */
    public Scope() {
        this.parent = null;
        this.bindings = new HashMap<>();
    }

    public void declare(String name, Value value) {
        if (bindings.containsKey(name)) {
            throw new IllegalStateException("Variable already declared in current scope");
        }
        bindings.put(name, value);
    }

    public void assign(String name, Value value) {
        if (bindings.containsKey(name)) {
            bindings.put(name, value);
        } else if (parent != null) {
            parent.assign(name, value);
        } else {
            throw new IllegalStateException("Undefined variable: " + name);
        }
    }

    public Value fetch(String name) {
        if (bindings.containsKey(name)) {
            return bindings.get(name);
        } else if (parent != null) {
            return parent.fetch(name);
        } else {
            throw new IllegalStateException("Undefined variable: " + name);
        }
    }

    public Scope getParent() {
        return parent;
    }

    public boolean contains(String name) {
        if (bindings.containsKey(name)) {
            return true;
        }
        return parent != null && parent.contains(name);
    }

    public void upsert(String name, Value value) {
        if (bindings.containsKey(name)) {
            assign(name, value);
        } else {
            declare(name, value);
        }
    }
}
