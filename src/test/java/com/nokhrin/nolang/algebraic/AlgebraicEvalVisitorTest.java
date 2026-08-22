package com.nokhrin.nolang.algebraic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nokhrin.nolang.common.executions.Result;
import com.nokhrin.nolang.common.executions.ValueResult;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.RealValue;
import java.io.PrintStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AlgebraicEvalVisitorTest {
  AlgebraicInterpreter interpreter;

  @BeforeEach
  public void setUp() {
    PrintStream output = new PrintStream(System.out);
    interpreter = new AlgebraicInterpreter(output);
  }

  static Stream<Arguments> arithmeticTestData() {
    return Stream.of(
        Arguments.of("1+2", new ValueResult(new IntValue(3))),
        Arguments.of("10 -   3  - 2 ", new ValueResult(new IntValue(5))),
        Arguments.of("2 * 3 + 4", new ValueResult(new IntValue(10))),
        Arguments.of("10 / 2 * 3", new ValueResult(new IntValue(15))),
        Arguments.of("1.0 + 2.5", new ValueResult(new RealValue(3.5))),
        Arguments.of("2 ^ 5", new ValueResult(new IntValue(32))),
        Arguments.of("2.0 ^ 2.0", new ValueResult(new RealValue(4))),
        Arguments.of("2.5 ^ 1.0", new ValueResult(new RealValue(2.5))),
        Arguments.of("|-2|", new ValueResult(new IntValue(2))),
        Arguments.of("|-2.5|", new ValueResult(new RealValue(2.5))),
        Arguments.of("5!", new ValueResult(new IntValue(120))),
        Arguments.of("(1.0 + 2.5)*2", new ValueResult(new RealValue(7.0))),
        Arguments.of("(1 + 2)*5", new ValueResult(new IntValue(15))));
  }

  @ParameterizedTest
  @MethodSource("arithmeticTestData")
  void evaluateArithmetic_actualEqualsExpected(String input, Result expected) {
    assertEquals(expected, interpreter.evaluate(input));
  }

  static Stream<Arguments> variableAssignments() {
    return Stream.of(
        Arguments.of(
            "y_1=1\nx_1 = y_1 + ( | 5!/10 - (+ 2) ^ 3 ^ 2 + -4*5 + .5 | )\nx_1",
            new ValueResult(new RealValue(520.5))),
        Arguments.of("a=10\nb   = 3\nc=  - 2\n a+b+c", new ValueResult(new IntValue(11))),
        Arguments.of("x=4\nx", new ValueResult(new IntValue(4))));
  }

  @ParameterizedTest
  @MethodSource("variableAssignments")
  void evaluateVariableAssignments_actualEqualsExpected(String input, Result expected) {
    assertEquals(expected, interpreter.evaluate(input));
  }

  @Test
  void evaluateUndefined_throwsException() {
    assertThrows(IllegalStateException.class, () -> interpreter.evaluate("a"));
  }

  @Test
  void evaluateDivisionByZero_throwsException() {
    assertThrows(ArithmeticException.class, () -> interpreter.evaluate("1/0"));
  }
}
