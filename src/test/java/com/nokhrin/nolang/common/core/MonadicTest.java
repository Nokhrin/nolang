package com.nokhrin.nolang.common.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class MonadicTest {
    private final ExecutionContext context =
            new ExecutionContext(new Scope(), new FunctionRegistry(Map.of()), List.of());

    @Test
    void evalPureReturnsValue() {
        Eval<Integer> valueEval = Eval.pure(1);
        EvalResult<Integer> actual = valueEval.run(context);

        switch (actual) {
            case EvalResult.Returned<Integer> returned -> {
                assertAll(
                        () -> assertEquals(1, returned.value()),
                        () -> assertEquals(context, returned.executionContext()));
            }
            case EvalResult.Interrupted<Integer> interrupted ->
                    fail("Unexpected interruption: " + interrupted.reason().message());
        }
    }

    @Test
    void evalRaiseErrorInterrupts() {
        Eval<Integer> failed = Eval.raiseError(new EvalError.ArithmeticError("test cause"));
        EvalResult<Integer> actual = failed.run(context);

        switch (actual) {
            case EvalResult.Interrupted<Integer> interrupted -> {
                assertAll(
                        () -> assertEquals(context, interrupted.executionContext()),
                        () -> assertEquals("test cause", interrupted.reason().message()));
            }
            case EvalResult.Returned<Integer> returned ->
                    fail("unexpected return: " + returned.value());
        }
    }

    @Test
    void interruptedEvalFlatMapDoesNotApplyFunction() {
        AtomicBoolean functionApplied = new AtomicBoolean(false);

        Eval<Integer> failed = Eval.raiseError(new EvalError.ArithmeticError("test cause"));

        Eval<Integer> actualEval =
                failed.flatMap(
                        value -> {
                            functionApplied.set(true);
                            return Eval.pure(value + 1);
                        });

        EvalResult<Integer> actual = actualEval.run(context);

        switch (actual) {
            case EvalResult.Interrupted<Integer> interrupted -> {
                assertAll(
                        () -> assertFalse(functionApplied.get()),
                        () -> assertEquals("test cause", interrupted.reason().message()));
            }
            case EvalResult.Returned<Integer> returned ->
                    fail("Unexpected return: " + returned.value());
        }
    }

    @Test
    void interruptedEvalMapDoesNotApplyFunction() {
        AtomicBoolean functionApplied = new AtomicBoolean(false);

        Eval<Integer> failed = Eval.raiseError(new EvalError.ArithmeticError("test cause"));

        Eval<Integer> actualEval =
                failed.map(
                        value -> {
                            functionApplied.set(true);
                            return value + 1;
                        });

        EvalResult<Integer> actual = actualEval.run(context);

        switch (actual) {
            case EvalResult.Interrupted<Integer> interrupted -> {
                assertAll(
                        () -> assertFalse(functionApplied.get()),
                        () -> assertEquals("test cause", interrupted.reason().message()));
            }
            case EvalResult.Returned<Integer> returned ->
                    fail("Unexpected return: " + returned.value());
        }
    }
}
