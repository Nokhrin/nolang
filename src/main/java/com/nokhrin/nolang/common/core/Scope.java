package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.values.Value;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Scope(Map<String, Value> bindings, Optional<Scope> parent) {

  /** Root */
  public Scope() {
    this(Map.of(), Optional.empty());
  }

  /** Child */
  public Scope(Scope parent) {
    this(Map.of(), Optional.of(parent));
  }

  private Scope withBinding(String name, Value value) {
    Map<String, Value> updatedBindings = new HashMap<>(bindings);
    updatedBindings.put(name, value);
    return new Scope(Map.copyOf(updatedBindings), parent);
  }

  /**
   * Creates var with value
   *
   * @param name
   * @param value
   * @return
   */
  public Either<ScopeError, Scope> define(String name, Value value) {
    if (bindings.containsKey(name)) {
      return Either.left(new ScopeError.DuplicatedVariable(name));
    }
    return Either.right(withBinding(name, value));
  }

  /**
   * Creates var without value
   *
   * @param name
   * @return
   */
  public Either<ScopeError, Scope> declare(String name) {
    return define(name, Value.VoidValue.INSTANCE);
  }

  /**
   * Updates existing vars
   *
   * @param name
   * @param value
   * @return
   */
  public Either<ScopeError, Scope> assign(String name, Value value) {
    if (bindings.containsKey(name)) {
      return Either.right(withBinding(name, value));
    }
    return parent
        .map(
            p ->
                p.assign(name, value)
                    .map(updatedParent -> new Scope(bindings, Optional.of(updatedParent))))
        .orElseGet(() -> Either.left(new ScopeError.UndefinedVariable(name)));
  }

  public Either<ScopeError, Scope> assignOrDefine(String name, Value value) {
    return assign(name, value).orElseGet(() -> define(name, value));
  }

  public Either<ScopeError, Value> lookup(String name) {
    Value value = bindings.get(name);
    if (value != null) {
      return Either.right(value);
    }
    return parent
        .map(p -> p.lookup(name))
        .orElseGet(() -> Either.left(new ScopeError.UndefinedVariable(name)));
  }

  public boolean isDefined(String name) {
    return lookup(name).isRight();
  }
}
