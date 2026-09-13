package com.nokhrin.nolang.common;

import com.nokhrin.nolang.common.operations.Numeric;
import com.nokhrin.nolang.common.values.NumericValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NumericExamplesTest {

    static Stream<Arguments> additionCases() {
        return Stream.of(
            Arguments.of(new NumericValue.Int(5), new NumericValue.Int(3), new NumericValue.Int(8)),
            Arguments.of(new NumericValue.Int(-5), new NumericValue.Int(3), new NumericValue.Int(-2)),
            Arguments.of(new NumericValue.Int(0), new NumericValue.Int(0), new NumericValue.Int(0)),
            Arguments.of(new NumericValue.Int(5), new NumericValue.Real(3.5), new NumericValue.Real(8.5)),
            Arguments.of(new NumericValue.Real(5.5), new NumericValue.Int(3), new NumericValue.Real(8.5)),
            Arguments.of(new NumericValue.Real(5.5), new NumericValue.Real(3.5), new NumericValue.Real(9.0)));
    }

    @ParameterizedTest
    @MethodSource("additionCases")
    public void add_validOperands_expectedValue(
        NumericValue left, NumericValue right, NumericValue expected) {
        assertEquals(expected, Numeric.add(left, right));
    }

    static Stream<Arguments> divisionCases() {
        return Stream.of(
            Arguments.of(new NumericValue.Int(10), new NumericValue.Int(2), new NumericValue.Int(5)),
            Arguments.of(new NumericValue.Int(5), new NumericValue.Int(2), new NumericValue.Int(2)),
            Arguments.of(new NumericValue.Real(5), new NumericValue.Int(2), new NumericValue.Real(2.5)),
            Arguments.of(new NumericValue.Real(5.5), new NumericValue.Int(2), new NumericValue.Real(2.75)),
            Arguments.of(new NumericValue.Int(42), new NumericValue.Int(1), new NumericValue.Int(42)),
            Arguments.of(new NumericValue.Int(42), new NumericValue.Int(-1), new NumericValue.Int(-42)));
    }

    @ParameterizedTest
    @MethodSource("divisionCases")
    public void div_validOperands_expectedValue(
        NumericValue left, NumericValue right, NumericValue expected) {
        assertEquals(Numeric.div(left, right), expected);
    }

    static Stream<Arguments> negationCases() {
        return Stream.of(
            Arguments.of(new NumericValue.Int(5), new NumericValue.Int(-5)),
            Arguments.of(new NumericValue.Int(-5), new NumericValue.Int(5)),
            Arguments.of(new NumericValue.Int(0), new NumericValue.Int(0)),
            Arguments.of(new NumericValue.Real(5.5), new NumericValue.Real(-5.5)),
            Arguments.of(new NumericValue.Real(-5.5), new NumericValue.Real(5.5)));
    }

    @ParameterizedTest
    @MethodSource("negationCases")
    public void neg_validOperand_expectedValue(NumericValue operand, NumericValue expected) {
        assertEquals(expected, Numeric.neg(operand));
    }

    @Test
    void div_byZero_throwsArithmeticException() {
        assertThrows(ArithmeticException.class, () -> Numeric.div(new NumericValue.Int(1), new NumericValue.Int(0)));
    }
}
