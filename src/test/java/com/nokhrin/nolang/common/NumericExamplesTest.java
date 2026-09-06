package com.nokhrin.nolang.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nokhrin.nolang.common.operations.Numeric;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.RealValue;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class NumericExamplesTest {

  static Stream<Arguments> additionCases() {
    return Stream.of(
        Arguments.of(new IntValue(5), new IntValue(3), new IntValue(8)),
        Arguments.of(new IntValue(-5), new IntValue(3), new IntValue(-2)),
        Arguments.of(new IntValue(0), new IntValue(0), new IntValue(0)),
        Arguments.of(new IntValue(5), new RealValue(3.5), new RealValue(8.5)),
        Arguments.of(new RealValue(5.5), new IntValue(3), new RealValue(8.5)),
        Arguments.of(new RealValue(5.5), new RealValue(3.5), new RealValue(9.0)));
  }

  @ParameterizedTest
  @MethodSource("additionCases")
  public void add_validOperands_expectedValue(
      NumericValue left, NumericValue right, NumericValue expected) {
    assertEquals(expected, Numeric.add(left, right));
  }

  static Stream<Arguments> divisionCases() {
    return Stream.of(
        Arguments.of(new IntValue(10), new IntValue(2), new IntValue(5)),
        Arguments.of(new IntValue(5), new IntValue(2), new IntValue(2)),
        Arguments.of(new RealValue(5), new IntValue(2), new RealValue(2.5)),
        Arguments.of(new RealValue(5.5), new IntValue(2), new RealValue(2.75)),
        Arguments.of(new IntValue(42), new IntValue(1), new IntValue(42)),
        Arguments.of(new IntValue(42), new IntValue(-1), new IntValue(-42)));
  }

  @ParameterizedTest
  @MethodSource("divisionCases")
  public void div_validOperands_expectedValue(
      NumericValue left, NumericValue right, NumericValue expected) {
    assertEquals(Numeric.div(left, right), expected);
  }

  static Stream<Arguments> negationCases() {
    return Stream.of(
        Arguments.of(new IntValue(5), new IntValue(-5)),
        Arguments.of(new IntValue(-5), new IntValue(5)),
        Arguments.of(new IntValue(0), new IntValue(0)),
        Arguments.of(new RealValue(5.5), new RealValue(-5.5)),
        Arguments.of(new RealValue(-5.5), new RealValue(5.5)));
  }

  @ParameterizedTest
  @MethodSource("negationCases")
  public void neg_validOperand_expectedValue(NumericValue operand, NumericValue expected) {
    assertEquals(expected, Numeric.neg(operand));
  }

  @Test
  void div_byZero_throwsArithmeticException() {
    assertThrows(ArithmeticException.class, () -> Numeric.div(new IntValue(1), new IntValue(0)));
  }
}
