package com.nokhrin.nolang.common.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.Value;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LogicalTest {
  private final ExecutionContext context =
      new ExecutionContext(new Scope(), new FunctionRegistry(Map.of()), List.of());

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
  @MethodSource("andExpressions")
  public void and_validOperands_expectedResult(
      Value.BoolValue left, Value.BoolValue right, Value.BoolValue expected) {
    Eval<Value.BoolValue> leftEval = Eval.pure(left);
    Eval<Value.BoolValue> rightEval = Eval.pure(right);
    Eval<Value.BoolValue> expectedEval = Eval.pure(expected);

    assertEquals(
        expectedEval.run(context),
        leftEval
            .flatMap(lBool -> rightEval.flatMap(rBool -> Logical.and(lBool, rBool)))
            .run(context));
  }

  @ParameterizedTest
  @MethodSource("orExpressions")
  public void or_validOperands_expectedResult(
      Value.BoolValue left, Value.BoolValue right, Value.BoolValue expected) {
    Eval<Value.BoolValue> leftEval = Eval.pure(left);
    Eval<Value.BoolValue> rightEval = Eval.pure(right);
    Eval<Value.BoolValue> expectedEval = Eval.pure(expected);

    assertEquals(
        expectedEval.run(context),
        leftEval
            .flatMap(lBool -> rightEval.flatMap(rBool -> Logical.or(lBool, rBool)))
            .run(context));
  }

  static Stream<Arguments> notExpressions() {
    return Stream.of(
        Arguments.of(new Value.BoolValue(true), new Value.BoolValue(false)),
        Arguments.of(new Value.BoolValue(false), new Value.BoolValue(true)));
  }

  @ParameterizedTest
  @MethodSource("notExpressions")
  public void not_validOperand_expectedResult(Value.BoolValue operand, Value.BoolValue expected) {
    Eval<Value.BoolValue> operandEval = Eval.pure(operand);
    Eval<Value.BoolValue> expectedEval = Eval.pure(expected);

    assertEquals(expectedEval.run(context), operandEval.flatMap(Logical::not).run(context));
  }
}
