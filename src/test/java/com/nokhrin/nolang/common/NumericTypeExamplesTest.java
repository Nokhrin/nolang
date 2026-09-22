package com.nokhrin.nolang.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nokhrin.nolang.common.operations.Numeric;
import com.nokhrin.nolang.common.values.NumericValue;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class NumericTypeExamplesTest {

  static Stream<Arguments> additionCases() {
    return Stream.of(
        Arguments.of(
            new NumericValue.IntValue(5),
            new NumericValue.IntValue(3),
            new NumericValue.IntValue(8)),
        Arguments.of(
            new NumericValue.IntValue(-5),
            new NumericValue.IntValue(3),
            new NumericValue.IntValue(-2)),
        Arguments.of(
            new NumericValue.IntValue(0),
            new NumericValue.IntValue(0),
            new NumericValue.IntValue(0)),
        Arguments.of(
            new NumericValue.IntValue(5),
            new NumericValue.RealValue(3.5),
            new NumericValue.RealValue(8.5)),
        Arguments.of(
            new NumericValue.RealValue(5.5),
            new NumericValue.IntValue(3),
            new NumericValue.RealValue(8.5)),
        Arguments.of(
            new NumericValue.RealValue(5.5),
            new NumericValue.RealValue(3.5),
            new NumericValue.RealValue(9.0)));
  }

  @ParameterizedTest
  @MethodSource("additionCases")
  public void add_validOperands_expectedValue(
      NumericValue left, NumericValue right, NumericValue expected) {
    assertEquals(expected, Numeric.add(left, right));
  }

  static Stream<Arguments> divisionCases() {
    return Stream.of(
        Arguments.of(
            new NumericValue.IntValue(10),
            new NumericValue.IntValue(2),
            new NumericValue.IntValue(5)),
        Arguments.of(
            new NumericValue.IntValue(5),
            new NumericValue.IntValue(2),
            new NumericValue.IntValue(2)),
        Arguments.of(
            new NumericValue.RealValue(5),
            new NumericValue.IntValue(2),
            new NumericValue.RealValue(2.5)),
        Arguments.of(
            new NumericValue.RealValue(5.5),
            new NumericValue.IntValue(2),
            new NumericValue.RealValue(2.75)),
        Arguments.of(
            new NumericValue.IntValue(42),
            new NumericValue.IntValue(1),
            new NumericValue.IntValue(42)),
        Arguments.of(
            new NumericValue.IntValue(42),
            new NumericValue.IntValue(-1),
            new NumericValue.IntValue(-42)));
  }

  @ParameterizedTest
  @MethodSource("divisionCases")
  public void div_validOperands_expectedValue(
      NumericValue left, NumericValue right, NumericValue expected) {
    assertEquals(Numeric.div(left, right), expected);
  }

  static Stream<Arguments> negationCases() {
    return Stream.of(
        Arguments.of(new NumericValue.IntValue(5), new NumericValue.IntValue(-5)),
        Arguments.of(new NumericValue.IntValue(-5), new NumericValue.IntValue(5)),
        Arguments.of(new NumericValue.IntValue(0), new NumericValue.IntValue(0)),
        Arguments.of(new NumericValue.RealValue(5.5), new NumericValue.RealValue(-5.5)),
        Arguments.of(new NumericValue.RealValue(-5.5), new NumericValue.RealValue(5.5)));
  }

  @ParameterizedTest
  @MethodSource("negationCases")
  public void neg_validOperand_expectedValue(NumericValue operand, NumericValue expected) {
    assertEquals(expected, Numeric.neg(operand));
  }

  @Test
  void div_byZero_throwsArithmeticException() {
    assertThrows(
        ArithmeticException.class,
        () -> Numeric.div(new NumericValue.IntValue(1), new NumericValue.IntValue(0)));
  }
}
