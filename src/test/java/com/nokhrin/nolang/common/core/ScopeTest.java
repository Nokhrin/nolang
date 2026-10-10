package com.nokhrin.nolang.common.core;

import static org.junit.jupiter.api.Assertions.*;

import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ScopeTest {

    @Test
    void defineNewVar_scopeUpdated() {
        Scope initialScope = new Scope();
        NumericValue.IntValue expected = new NumericValue.IntValue(1);
        Either<ScopeError, Scope> result = initialScope.define("x", new NumericValue.IntValue(1));

        assertAll(
                () ->
                        assertEquals(
                                Either.right(expected),
                                result.flatMap(scope -> scope.lookup("x"))));
    }

    @Test
    void assignNewVar_scopeUpdated() {

        Scope initialScope = new Scope();
        Scope scopeWithX =
                initialScope
                        .define("x", new NumericValue.IntValue(1))
                        .rightOptional()
                        .orElseThrow(() -> new AssertionError("Define failed"));
        Either<ScopeError, Scope> actual = scopeWithX.assign("x", new NumericValue.IntValue(2));

        assertAll(
                () ->
                        assertEquals(
                                Either.right(new NumericValue.IntValue(2)),
                                actual.flatMap(scope -> scope.lookup("x"))));
    }

    @Test
    void assignOrDefineNewVar_scopeUpdated() {
        Scope initialScope = new Scope();
        Either<ScopeError, Scope> actual =
                initialScope.assignOrDefine("x", new NumericValue.IntValue(1));

        assertEquals(
                Either.right(new NumericValue.IntValue(1)),
                actual.flatMap(scope -> scope.lookup("x")));
    }

    @Test
    void declareNew_scopeUpdated() {
        Scope initialScope = new Scope();
        Either<ScopeError, Scope> actual = initialScope.declare("x");
        Scope actualScope = actual.rightOptional().orElseThrow();

        assertAll(
                () -> assertEquals(Value.VoidValue.INSTANCE, actualScope.bindings().get("x")),
                () -> assertEquals(Optional.empty(), actualScope.parent()));
    }

    @Test
    void assignNotDefined_returnsUndefinedVariable() {
        Scope initialScope = new Scope();
        NumericValue.IntValue expected = new NumericValue.IntValue(1);
        Either<ScopeError, Scope> result = initialScope.assign("x", new NumericValue.IntValue(1));

        assertAll(
                () ->
                        assertEquals(
                                Either.left(new ScopeError.UndefinedVariable("x")),
                                initialScope.lookup("x")));
    }

    @Test
    void defineDuplicated_returnsDuplicatedVariable() {
        Scope initialScope = new Scope();
        Either<ScopeError, Scope> scopeWithX =
                initialScope.define("x", new NumericValue.IntValue(1));
        Either<ScopeError, Scope> actual =
                scopeWithX.flatMap(scope -> scope.define("x", new NumericValue.IntValue(2)));

        assertEquals(new ScopeError.DuplicatedVariable("x"), actual.leftOptional().orElseThrow());
    }

    @Test
    void assignInParent_reassignInChild_updatesParentBinding() {
        Scope parentScope = new Scope();
        Scope parentScopeWithX =
                parentScope
                        .define("x", new NumericValue.IntValue(1))
                        .rightOptional()
                        .orElseThrow(() -> new AssertionError("Define failed"));

        Scope childScope = new Scope(parentScopeWithX);
        Either<ScopeError, Scope> childScopeNoLocalX =
                childScope.assign("x", new NumericValue.IntValue(2));

        assertAll(
                () ->
                        assertEquals(
                                Either.right(new NumericValue.IntValue(1)),
                                parentScopeWithX.lookup("x")),
                () ->
                        assertFalse(
                                childScopeNoLocalX
                                        .rightOptional()
                                        .orElseThrow()
                                        .bindings()
                                        .containsKey("x")),
                () ->
                        assertEquals(
                                Either.right(new NumericValue.IntValue(2)),
                                childScopeNoLocalX.flatMap(scope -> scope.lookup("x"))),
                () ->
                        assertEquals(
                                Either.right(new NumericValue.IntValue(2)),
                                childScopeNoLocalX.flatMap(
                                        scope -> scope.parent().orElseThrow().lookup("x"))));
    }

    @Test
    void lookupUndefined_returnsUndefinedVariable() {
        Scope scope = new Scope();

        assertAll(
                () ->
                        assertEquals(
                                Either.left(new ScopeError.UndefinedVariable("x")),
                                scope.lookup("x")));
    }
}
