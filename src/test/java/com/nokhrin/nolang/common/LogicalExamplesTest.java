package com.nokhrin.nolang.common;

import static com.nokhrin.nolang.common.operations.Logical.*;
import static com.nokhrin.nolang.common.operations.Logical.LogicalOperation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.RealValue;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LogicalExamplesTest {

  static Stream<Arguments> andExpressions() {
    return Stream.of(
        Arguments.of(new BoolValue(true), new BoolValue(true), new BoolValue(true)),
        Arguments.of(new BoolValue(true), new BoolValue(false), new BoolValue(false)),
        Arguments.of(new BoolValue(false), new BoolValue(true), new BoolValue(false)),
        Arguments.of(new BoolValue(false), new BoolValue(false), new BoolValue(false)));
  }

  @ParameterizedTest
  @MethodSource("andExpressions")
  public void and_validOperands_expectedResult(
      BoolValue left, BoolValue right, BoolValue expected) {
    assertEquals(expected, and(left, right));
  }

  static Stream<Arguments> orExpressions() {
    return Stream.of(
        Arguments.of(new BoolValue(true), new BoolValue(true), new BoolValue(true)),
        Arguments.of(new BoolValue(true), new BoolValue(false), new BoolValue(true)),
        Arguments.of(new BoolValue(false), new BoolValue(true), new BoolValue(true)),
        Arguments.of(new BoolValue(false), new BoolValue(false), new BoolValue(false)));
  }

  @ParameterizedTest
  @MethodSource("orExpressions")
  public void or_validOperands_expectedResult(BoolValue left, BoolValue right, BoolValue expected) {
    assertEquals(expected, or(left, right));
  }

  @Test
  public void and_nonBoolLeft_throwsIllegalStateException() {
    assertThrows(IllegalStateException.class, () -> and(new IntValue(5), new BoolValue(true)));
  }

  @Test
  public void or_nonBoolRight_throwsIllegalStateException() {
    assertThrows(IllegalStateException.class, () -> or(new BoolValue(true), new IntValue(5)));
  }

  static Stream<Arguments> notExpressions() {
    return Stream.of(
        Arguments.of(new BoolValue(true), new BoolValue(false)),
        Arguments.of(new BoolValue(false), new BoolValue(true)));
  }

  @ParameterizedTest
  @MethodSource("notExpressions")
  public void not_validOperand_expectedResult(BoolValue operand, BoolValue expected) {
    assertEquals(expected, not(operand));
  }

  @Test
  public void not_nonBool_throwsIllegalStateException() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          not(new IntValue(5));
        });
  }

  static Stream<Arguments> comparisonExpressions() {
    return Stream.of(
        Arguments.of(new IntValue(5), GT, new IntValue(3), new BoolValue(true)),
        Arguments.of(new IntValue(3), GT, new IntValue(5), new BoolValue(false)),
        Arguments.of(new IntValue(5), EQ, new IntValue(5), new BoolValue(true)),
        Arguments.of(new IntValue(5), NEQ, new IntValue(3), new BoolValue(true)),
        Arguments.of(new IntValue(5), GEQ, new IntValue(5), new BoolValue(true)),
        Arguments.of(new IntValue(5), LEQ, new IntValue(3), new BoolValue(false)),
        Arguments.of(new RealValue(5.5), GT, new RealValue(3.5), new BoolValue(true)),
        Arguments.of(new IntValue(5), GT, new RealValue(3.5), new BoolValue(true)));
  }

  @ParameterizedTest
  @MethodSource("comparisonExpressions")
  public void compare_validOperands_expectedResult(
      NumericValue left, LogicalOperation op, NumericValue right, BoolValue expected) {
    assertEquals(expected, compare(left, op, right));
  }
}
