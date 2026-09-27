package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class NumericTest {
  private final ExecutionContext context = new ExecutionContext(
    new Scope(),
    new FunctionRegistry(Map.of()),
    List.of()
  );


  static Stream<Arguments> additionSubtractionMultiplicationCases() {
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
  @MethodSource("additionSubtractionMultiplicationCases")
  public void add_validOperands_expectedValue(
      NumericValue left, NumericValue right, NumericValue expected) {

    Eval<NumericValue> leftEval = Eval.pure(left);
    Eval<NumericValue> rightEval = Eval.pure(right);
    Eval<NumericValue> expectedEval = Eval.pure(expected);

    assertEquals(
      expectedEval.run(context),
      leftEval.flatMap(a ->
        rightEval.flatMap(b ->
          Numeric.add(a, b))).run(context)
    );
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
    Eval<NumericValue> leftEval = Eval.pure(left);
    Eval<NumericValue> rightEval = Eval.pure(right);
    Eval<NumericValue> expectedEval = Eval.pure(expected);

    assertEquals(
      expectedEval.run(context),
      leftEval.flatMap(a ->
        rightEval.flatMap(b ->
          Numeric.div(a, b))).run(context)
    );
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
    Eval<NumericValue> operandEval = Eval.pure(operand);
    Eval<NumericValue> expectedEval = Eval.pure(expected);

    assertEquals(
      expectedEval.run(context),
      operandEval.flatMap(Numeric::neg).run(context)
    );
  }

  @Test
  void divisionByZero_throwsArithmeticException() {
    NumericValue dividend = new NumericValue.IntValue(1);
    NumericValue divisor = new NumericValue.IntValue(0);

    Eval<NumericValue> divisionByZero = Numeric.div(dividend, divisor);

    EvalResult<NumericValue> result = divisionByZero.run(context);

    switch (result) {
      case EvalResult.Interrupted<NumericValue> interrupted ->
        assertInstanceOf(InterruptReason.class, interrupted.reason());
      case EvalResult.Returned<NumericValue> _ -> throw new AssertionError("Interruption expected, value returned");
    }
  }
}
