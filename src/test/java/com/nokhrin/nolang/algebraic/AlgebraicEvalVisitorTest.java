package com.nokhrin.nolang.algebraic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AlgebraicEvalVisitorTest {
  ExecutionContext context;
  AlgebraicInterpreter interpreter;

  static Stream<Arguments> arithmeticCases() {
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

  static Stream<ProgramTestCase> assignmentProgramCases() {
    return Stream.of(
        new ProgramTestCase(
            "assignment creates variable",
            "x = 4",
            Value.VoidValue.INSTANCE,
            Map.of("x", new NumericValue.IntValue(4))),
        new ProgramTestCase(
            "assignment updates variable",
            """
          x = 1
          x = 2
          """,
            Value.VoidValue.INSTANCE,
            Map.of("x", new NumericValue.IntValue(2))),
        new ProgramTestCase(
            "assignment then read",
            """
          x = 4
          x
          """,
            new NumericValue.IntValue(4),
            Map.of("x", new NumericValue.IntValue(4))));
  }

  static Stream<Arguments> errorCases() {
    return Stream.of(
        Arguments.of(
            "1/0", new InterruptReason.Error(new EvalError.ArithmeticError("Division by zero"))),
        Arguments.of("v", new InterruptReason.Error(new ScopeError.UndefinedVariable("v"))));
  }

  @BeforeEach
  public void setUp() {
    Scope scope = new Scope();
    FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
    List<String> outputBuffer = List.of();
    context = new ExecutionContext(scope, registry, outputBuffer);
    interpreter = AlgebraicInterpreter.create();
  }

  @ParameterizedTest
  @MethodSource("arithmeticCases")
  void evaluateArithmetic_actualEqualsExpected(String input, NumericValue expected) {
    EvalResult<Value> actual = interpreter.evaluate(input, context);
    switch (actual) {
      case EvalResult.Returned<Value> returned -> assertEquals(expected, returned.value());
      case EvalResult.Interrupted<Value> interrupted ->
          fail("Interrupted, reason: " + interrupted.reason().message());
    }
  }

  @ParameterizedTest
  @MethodSource("assignmentProgramCases")
  void assignmentCreatesVariable(ProgramTestCase testCase) {
    EvalResult<Value> actual = interpreter.evaluate(testCase.source(), context);

    switch (actual) {
      case EvalResult.Returned<Value> returned -> {
        assertEquals(testCase.expectedResult(), returned.value());
        testCase
            .expectedScope()
            .forEach(
                (varName, varValueExpected) ->
                    assertEquals(
                        Either.right(varValueExpected),
                        returned.executionContext().scope().lookup(varName)));
      }
      case EvalResult.Interrupted<Value> interrupted ->
          fail("interrupted: " + interrupted.reason().message());
    }
  }

  @ParameterizedTest
  @MethodSource("errorCases")
  void invalidExpressionReturnsInterrupt(String input, InterruptReason.Error expected) {
    EvalResult<Value> actual = interpreter.evaluate(input, context);
    switch (actual) {
      case EvalResult.Interrupted<Value> interrupted ->
          assertEquals(expected, interrupted.reason());
      case EvalResult.Returned<Value> returned ->
          fail("Unexpectedly returned: " + returned.value());
    }
  }
}
