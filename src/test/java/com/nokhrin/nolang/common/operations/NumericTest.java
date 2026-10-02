package com.nokhrin.nolang.common.operations;

import static org.junit.jupiter.api.Assertions.*;

import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class NumericTest {
  private final ExecutionContext context =
      new ExecutionContext(new Scope(), new FunctionRegistry(Map.of()), List.of());

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
        leftEval.flatMap(a -> rightEval.flatMap(b -> Numeric.add(a, b))).run(context));
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
        leftEval.flatMap(a -> rightEval.flatMap(b -> Numeric.div(a, b))).run(context));
  }

  static Stream<Arguments> powerCases() {
    return Stream.of(
        Arguments.of(
            new NumericValue.IntValue(0),
            new NumericValue.IntValue(0),
            new NumericValue.IntValue(1)),
        Arguments.of(
            new NumericValue.IntValue(0),
            new NumericValue.IntValue(1),
            new NumericValue.IntValue(0)),
        Arguments.of(
            new NumericValue.RealValue(1),
            new NumericValue.IntValue(1),
            new NumericValue.RealValue(1)),
        Arguments.of(
            new NumericValue.RealValue(1.5),
            new NumericValue.IntValue(1),
            new NumericValue.RealValue(1.5)));
  }

  static Stream<Arguments> factorialCases() {
    return Stream.of(
        Arguments.of(new NumericValue.IntValue(0), new NumericValue.IntValue(1)),
        Arguments.of(new NumericValue.IntValue(5), new NumericValue.IntValue(120)));
  }

  static Stream<Arguments> percentCases() {
    return Stream.of(
        Arguments.of(new NumericValue.IntValue(100), new NumericValue.RealValue(1)),
        Arguments.of(new NumericValue.IntValue(0), new NumericValue.RealValue(0)),
        Arguments.of(new NumericValue.IntValue(50), new NumericValue.RealValue(0.5)),
        Arguments.of(new NumericValue.RealValue(12.5), new NumericValue.RealValue(0.125)));
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

    assertEquals(expectedEval.run(context), operandEval.flatMap(Numeric::neg).run(context));
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
      case EvalResult.Returned<NumericValue> _ ->
          throw new AssertionError("Interruption expected, value returned");
    }
  }

  @Test
  void add_longMaxPlusOne_overflow() {
    Eval<NumericValue> longMaxEval = Eval.pure(new NumericValue.IntValue(Long.MAX_VALUE));
    Eval<NumericValue> oneEval = Eval.pure(new NumericValue.IntValue(1));
    EvalResult<NumericValue> result =
        longMaxEval
            .flatMap(longMax -> oneEval.flatMap(one -> Numeric.add(longMax, one)))
            .run(context);

    switch (result) {
      case EvalResult.Interrupted<NumericValue> interrupted ->
          assertEquals(
              new InterruptReason.Error(new EvalError.ArithmeticError("Integer overflow")),
              interrupted.reason());
      case EvalResult.Returned<NumericValue> returned ->
          fail("unexpected return: " + returned.value());
    }
  }

  @Test
  void mul_longMaxMulByTwo_overflow() {
    Eval<NumericValue> longMaxEval = Eval.pure(new NumericValue.IntValue(Long.MAX_VALUE));
    Eval<NumericValue> twoEval = Eval.pure(new NumericValue.IntValue(2));
    EvalResult<NumericValue> result =
        longMaxEval
            .flatMap(longMax -> twoEval.flatMap(one -> Numeric.mul(longMax, one)))
            .run(context);

    switch (result) {
      case EvalResult.Interrupted<NumericValue> interrupted ->
          assertEquals(
              new InterruptReason.Error(new EvalError.ArithmeticError("Integer overflow")),
              interrupted.reason());
      case EvalResult.Returned<NumericValue> returned ->
          fail("unexpected return: " + returned.value());
    }
  }

  @Test
  void sub_longMinMinusOne_overflow() {
    Eval<NumericValue> longMinEval = Eval.pure(new NumericValue.IntValue(Long.MIN_VALUE));
    Eval<NumericValue> oneEval = Eval.pure(new NumericValue.IntValue(1));
    EvalResult<NumericValue> result =
        longMinEval
            .flatMap(longMin -> oneEval.flatMap(one -> Numeric.sub(longMin, one)))
            .run(context);

    switch (result) {
      case EvalResult.Interrupted<NumericValue> interrupted ->
          assertEquals(
              new InterruptReason.Error(new EvalError.ArithmeticError("Integer overflow")),
              interrupted.reason());
      case EvalResult.Returned<NumericValue> returned ->
          fail("unexpected return: " + returned.value());
    }
  }

  @ParameterizedTest
  @MethodSource("powerCases")
  void power_validInput_expectedOutput(
      NumericValue base, NumericValue exponent, NumericValue expected) {
    Eval<NumericValue> baseEval = Eval.pure(base);
    Eval<NumericValue> exponentEval = Eval.pure(exponent);
    Eval<NumericValue> result =
        baseEval.flatMap(b -> exponentEval.flatMap(exp -> Numeric.pow(b, exp)));

    assertEquals(Eval.pure(expected).run(context), result.run(context));
  }

  @Test
  void power_negBaseDecimalExponent_ArithmeticError() {
    Eval<NumericValue> negBase = Eval.pure(new NumericValue.IntValue(-1));
    Eval<NumericValue> decimalExponent = Eval.pure(new NumericValue.RealValue(1.5));
    EvalResult<NumericValue> result =
        negBase
            .flatMap(base -> decimalExponent.flatMap(exponent -> Numeric.pow(base, exponent)))
            .run(context);

    switch (result) {
      case EvalResult.Interrupted<NumericValue> interrupted ->
          assertEquals(
              new InterruptReason.Error(
                  new EvalError.ArithmeticError(
                      "Power of negative base with non-integer exponent is not defined")),
              interrupted.reason());
      case EvalResult.Returned<NumericValue> returned ->
          fail("Unexpected returned: " + returned.value());
    }
  }

  @ParameterizedTest
  @MethodSource("factorialCases")
  void factorial_validInput_expectedOutput(NumericValue operand, NumericValue expected) {
    Eval<NumericValue> result = Numeric.fact(operand);
    assertEquals(Eval.pure(expected).run(context), result.run(context));
  }

  @Test
  void factorial_neg_ArithmeticError() {
    Eval<NumericValue> negNum = Eval.pure(new NumericValue.IntValue(-1));
    EvalResult<NumericValue> result = negNum.flatMap(Numeric::fact).run(context);

    switch (result) {
      case EvalResult.Interrupted<NumericValue> interrupted ->
          assertEquals(
              new InterruptReason.Error(
                  new EvalError.ArithmeticError("Factorial of negative number")),
              interrupted.reason());
      case EvalResult.Returned<NumericValue> returned ->
          fail("Unexpected returned: " + returned.value());
    }
  }

  @Test
  void factorial_decimal_ArithmeticError() {
    Eval<NumericValue> decNum = Eval.pure(new NumericValue.RealValue(1.5));
    EvalResult<NumericValue> result = decNum.flatMap(Numeric::fact).run(context);

    switch (result) {
      case EvalResult.Interrupted<NumericValue> interrupted ->
          assertEquals(
              new InterruptReason.Error(new EvalError.ArithmeticError("Factorial of non-integer")),
              interrupted.reason());
      case EvalResult.Returned<NumericValue> returned ->
          fail("Unexpected returned: " + returned.value());
    }
  }

  @Test
  void factorialLongOverflow() {
    Eval<NumericValue> numberEval = Eval.pure(new NumericValue.IntValue(21));
    EvalResult<NumericValue> result = numberEval.flatMap(Numeric::fact).run(context);

    switch (result) {
      case EvalResult.Interrupted<NumericValue> interrupted ->
          assertEquals(
              new InterruptReason.Error(new EvalError.ArithmeticError("Integer overflow")),
              interrupted.reason());
      case EvalResult.Returned<NumericValue> returned ->
          fail("unexpected return: " + returned.value());
    }
  }

  @ParameterizedTest
  @MethodSource("percentCases")
  void percent_validInput_expectedOutput(NumericValue operand, NumericValue expected) {
    Eval<NumericValue> result = Numeric.percent(operand);
    assertEquals(Eval.pure(expected).run(context), result.run(context));
  }
}
