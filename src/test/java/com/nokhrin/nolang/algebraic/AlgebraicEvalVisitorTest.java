package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.core.EvalResult;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.core.FunctionRegistry;
import com.nokhrin.nolang.common.core.Scope;
import com.nokhrin.nolang.common.values.NumericValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AlgebraicEvalVisitorTest {
    ExecutionContext executionContext;
    AlgebraicInterpreter interpreter;

    @BeforeEach
    public void setUp() {
        PrintStream output = new PrintStream(System.out);
        Scope scope = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        List<String> outputBuffer = List.of();
        executionContext = new ExecutionContext(scope, registry, outputBuffer);
        interpreter = AlgebraicInterpreter.monadic();
    }

    static Stream<Arguments> arithmeticTestData() {
        return Stream.of(
            Arguments.of("1+2", new NumericValue.IntValue(3)),
            Arguments.of("10 -   3  - 2 ", new NumericValue.IntValue(5)),
            Arguments.of("2 * 3 + 4", new NumericValue.IntValue(10)),
            Arguments.of("10 / 2 * 3", new NumericValue.IntValue(15)),
            Arguments.of("1.0 + 2.5", new NumericValue.RealValue(3.5)),
            Arguments.of("2 ^ 5", new NumericValue.IntValue(32)),
            Arguments.of("2.0 ^ 2.0", new NumericValue.RealValue(4)),
            Arguments.of("2.5 ^ 1.0", new NumericValue.RealValue(2.5)),
            Arguments.of("|-2|", new NumericValue.IntValue(2)),
            Arguments.of("|-2.5|", new NumericValue.RealValue(2.5)),
            Arguments.of("5!", new NumericValue.IntValue(120)),
            Arguments.of("(1.0 + 2.5)*2", new NumericValue.RealValue(7.0)),
            Arguments.of("(1 + 2)*5", new NumericValue.IntValue(15)));
    }

    @ParameterizedTest
    @MethodSource("arithmeticTestData")
    void evaluateArithmetic_actualEqualsExpected(String input, EvalResult<NumericValue> expected) {
        assertEquals(expected, interpreter.interpret(input, executionContext));
    }

    static Stream<Arguments> variableAssignments() {
        return Stream.of(
            Arguments.of(
                "y_1=1\nx_1 = y_1 + ( | 5!/10 - (+ 2) ^ 3 ^ 2 + -4*5 + .5 | )\nx_1",
                new NumericValue.RealValue(520.5)),
            Arguments.of("a=10\nb   = 3\nc=  - 2\n a+b+c", new NumericValue.IntValue(11)),
            Arguments.of("x=4\nx", new NumericValue.IntValue(4)));
    }

    @ParameterizedTest
    @MethodSource("variableAssignments")
    void evaluateVariableAssignments_actualEqualsExpected(String input, EvalResult expected) {
        assertEquals(expected, interpreter.interpret(input, executionContext));
    }

    @Test
    void evaluateUndefined_throwsException() {
        assertThrows(IllegalStateException.class, () -> interpreter.interpret("a", executionContext));
    }

    @Test
    void evaluateDivisionByZero_throwsException() {
        assertThrows(ArithmeticException.class, () -> interpreter.interpret("1/0", executionContext));
    }
}
