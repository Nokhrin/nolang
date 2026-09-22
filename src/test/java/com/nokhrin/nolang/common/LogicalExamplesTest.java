package com.nokhrin.nolang.common;

import static com.nokhrin.nolang.common.operations.Logical.*;
import static com.nokhrin.nolang.common.operations.NumericComparisonOperation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nokhrin.nolang.common.operations.NumericComparisonOperation;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LogicalExamplesTest {

  static Stream<Arguments> andExpressions() {
    return Stream.of(
        Arguments.of(
            new Value.BoolValue(true), new Value.BoolValue(true), new Value.BoolValue(true)),
        Arguments.of(
            new Value.BoolValue(true), new Value.BoolValue(false), new Value.BoolValue(false)),
        Arguments.of(
            new Value.BoolValue(false), new Value.BoolValue(true), new Value.BoolValue(false)),
        Arguments.of(
            new Value.BoolValue(false), new Value.BoolValue(false), new Value.BoolValue(false)));
  }

  @ParameterizedTest
  @MethodSource("andExpressions")
  public void and_validOperands_expectedResult(
      Value.BoolValue left, Value.BoolValue right, Value.BoolValue expected) {
    assertEquals(expected, and(left, right));
  }

  static Stream<Arguments> orExpressions() {
    return Stream.of(
        Arguments.of(
            new Value.BoolValue(true), new Value.BoolValue(true), new Value.BoolValue(true)),
        Arguments.of(
            new Value.BoolValue(true), new Value.BoolValue(false), new Value.BoolValue(true)),
        Arguments.of(
            new Value.BoolValue(false), new Value.BoolValue(true), new Value.BoolValue(true)),
        Arguments.of(
            new Value.BoolValue(false), new Value.BoolValue(false), new Value.BoolValue(false)));
  }

  @ParameterizedTest
  @MethodSource("orExpressions")
  public void or_validOperands_expectedResult(
      Value.BoolValue left, Value.BoolValue right, Value.BoolValue expected) {
    assertEquals(expected, or(left, right));
  }

  static Stream<Arguments> notExpressions() {
    return Stream.of(
        Arguments.of(new Value.BoolValue(true), new Value.BoolValue(false)),
        Arguments.of(new Value.BoolValue(false), new Value.BoolValue(true)));
  }

  @ParameterizedTest
  @MethodSource("notExpressions")
  public void not_validOperand_expectedResult(Value.BoolValue operand, Value.BoolValue expected) {
    assertEquals(expected, not(operand));
  }

  static Stream<Arguments> comparisonExpressions() {
    return Stream.of(
        Arguments.of(
            new NumericValue.IntValue(5),
            GT,
            new NumericValue.IntValue(3),
            new Value.BoolValue(true)),
        Arguments.of(
            new NumericValue.IntValue(3),
            GT,
            new NumericValue.IntValue(5),
            new Value.BoolValue(false)),
        Arguments.of(
            new NumericValue.IntValue(5),
            EQ,
            new NumericValue.IntValue(5),
            new Value.BoolValue(true)),
        Arguments.of(
            new NumericValue.IntValue(5),
            NEQ,
            new NumericValue.IntValue(3),
            new Value.BoolValue(true)),
        Arguments.of(
            new NumericValue.IntValue(5),
            GEQ,
            new NumericValue.IntValue(5),
            new Value.BoolValue(true)),
        Arguments.of(
            new NumericValue.IntValue(5),
            LEQ,
            new NumericValue.IntValue(3),
            new Value.BoolValue(false)),
        Arguments.of(
            new NumericValue.RealValue(5.5),
            GT,
            new NumericValue.RealValue(3.5),
            new Value.BoolValue(true)),
        Arguments.of(
            new NumericValue.IntValue(5),
            GT,
            new NumericValue.RealValue(3.5),
            new Value.BoolValue(true)));
  }

  @ParameterizedTest
  @MethodSource("comparisonExpressions")
  public void compare_validOperands_expectedResult(
      NumericValue left,
      NumericComparisonOperation op,
      NumericValue right,
      Value.BoolValue expected) {
    assertEquals(expected, compare(left, op, right));
  }
}
