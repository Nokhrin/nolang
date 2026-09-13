package com.nokhrin.nolang.common;

import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.nokhrin.nolang.common.operations.Logical.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogicalExamplesTest {

    static Stream<Arguments> andExpressions() {
        return Stream.of(
            Arguments.of(new Value.Bool(true), new Value.Bool(true), new Value.Bool(true)),
            Arguments.of(new Value.Bool(true), new Value.Bool(false), new Value.Bool(false)),
            Arguments.of(new Value.Bool(false), new Value.Bool(true), new Value.Bool(false)),
            Arguments.of(new Value.Bool(false), new Value.Bool(false), new Value.Bool(false)));
    }

    @ParameterizedTest
    @MethodSource("andExpressions")
    public void and_validOperands_expectedResult(
        Value.Bool left, Value.Bool right, Value.Bool expected) {
        assertEquals(expected, and(left, right));
    }

    static Stream<Arguments> orExpressions() {
        return Stream.of(
            Arguments.of(new Value.Bool(true), new Value.Bool(true), new Value.Bool(true)),
            Arguments.of(new Value.Bool(true), new Value.Bool(false), new Value.Bool(true)),
            Arguments.of(new Value.Bool(false), new Value.Bool(true), new Value.Bool(true)),
            Arguments.of(new Value.Bool(false), new Value.Bool(false), new Value.Bool(false)));
    }

    @ParameterizedTest
    @MethodSource("orExpressions")
    public void or_validOperands_expectedResult(Value.Bool left, Value.Bool right, Value.Bool expected) {
        assertEquals(expected, or(left, right));
    }

    @Test
    public void and_nonBoolLeft_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> and(new NumericValue.Int(5), new Value.Bool(true)));
    }

    @Test
    public void or_nonBoolRight_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> or(new Value.Bool(true), new NumericValue.Int(5)));
    }

    static Stream<Arguments> notExpressions() {
        return Stream.of(
            Arguments.of(new Value.Bool(true), new Value.Bool(false)),
            Arguments.of(new Value.Bool(false), new Value.Bool(true)));
    }

    @ParameterizedTest
    @MethodSource("notExpressions")
    public void not_validOperand_expectedResult(Value.Bool operand, Value.Bool expected) {
        assertEquals(expected, not(operand));
    }

    @Test
    public void not_nonBool_throwsIllegalStateException() {
        assertThrows(
            IllegalStateException.class,
            () -> {
                not(new NumericValue.Int(5));
            });
    }

    static Stream<Arguments> comparisonExpressions() {
        return Stream.of(
            Arguments.of(new NumericValue.Int(5), GT, new NumericValue.Int(3), new Value.Bool(true)),
            Arguments.of(new NumericValue.Int(3), GT, new NumericValue.Int(5), new Value.Bool(false)),
            Arguments.of(new NumericValue.Int(5), EQ, new NumericValue.Int(5), new Value.Bool(true)),
            Arguments.of(new NumericValue.Int(5), NEQ, new NumericValue.Int(3), new Value.Bool(true)),
            Arguments.of(new NumericValue.Int(5), GEQ, new NumericValue.Int(5), new Value.Bool(true)),
            Arguments.of(new NumericValue.Int(5), LEQ, new NumericValue.Int(3), new Value.Bool(false)),
            Arguments.of(new NumericValue.Real(5.5), GT, new NumericValue.Real(3.5), new Value.Bool(true)),
            Arguments.of(new NumericValue.Int(5), GT, new NumericValue.Real(3.5), new Value.Bool(true)));
    }

    @ParameterizedTest
    @MethodSource("comparisonExpressions")
    public void compare_validOperands_expectedResult(
        NumericValue left, LogicalOperation op, NumericValue right, Value.Bool expected) {
        assertEquals(expected, compare(left, op, right));
    }
}
