package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.values.VoidValue;
import com.nokhrin.nolang.functional.Either;
import com.nokhrin.nolang.functional.ScopeError;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Scope(Map<String, Value> bindings, Optional<Scope> parent) {

    /**
     * Root
     */
    public Scope() {
        this(Map.of(), Optional.empty());
    }

    /**
     * Child
     *
     * @param parent
     */
    public Scope(Scope parent) {
        this(Map.of(), Optional.of(parent));
    }

    private Scope withBinding(String name, Value value) {
        Map<String, Value> updatedBindings = new HashMap<>(bindings);
        updatedBindings.put(name, value);
        return new Scope(Map.copyOf(updatedBindings), parent);
    }

    public Either<ScopeError, Scope> define(String name, Value value) {
        if (bindings.containsKey(name)) {
            return Either.left(new ScopeError.Duplicated(name));
        }
        return Either.right(withBinding(name, value));
    }

    public Either<ScopeError, Scope> declare(String name) {
        return define(name, VoidValue.INSTANCE);
    }

    public Either<ScopeError, Scope> assign(String name, Value value) {
        if (bindings.containsKey(name)) {
            return Either.right(withBinding(name, value));
        }
        return parent
            .map(
                p ->
                    p.assign(name, value)
                        .map(updatedParent -> new Scope(bindings, Optional.of(updatedParent))))
            .orElseGet(() -> Either.left(new ScopeError.Undefined(name)));
    }

    public Either<ScopeError, Scope> assignOrDefine(String name, Value value) {
        return assign(name, value).orElseGet(() -> define(name, value));
    }

    public Optional<Value> lookup(String name) {
        if (bindings.containsKey(name)) {
            return Optional.of(bindings.get(name));
        }
        return parent.flatMap(p -> p.lookup(name));
    }

    public boolean isVisible(String name) {
        if (bindings.containsKey(name)) {
            return true;
        }
        return parent.map(p -> p.isVisible(name)).orElse(false);
    }
}
