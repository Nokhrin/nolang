package com.nokhrin.nolang.algebraic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import com.nokhrin.nolang.common.values.DoubleValue;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.Value;
import java.io.PrintStream;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AlgebraicEvalVisitorTest {
  AlgebraicEvaluator evaluator = null;

  @BeforeMethod
  private void setUp() {
    evaluator = new AlgebraicEvaluator(new PrintStream(System.out));
  }

  @DataProvider
  public Object[][] arithmeticTestData() {
    return new Object[][] {
      {"1+2", new IntValue(3)},
      {"10 -   3  - 2 ", new IntValue(5)},
      {"2 * 3 + 4", new IntValue(10)},
      {"10 / 2 * 3", new IntValue(15)},
      {"1.0 + 2.5", new DoubleValue(3.5)},
      {"2 ^ 5", new IntValue(32)},
      {"2.0 ^ 2.0", new IntValue(4)},
      {"2.5 ^ 1.0", new DoubleValue(2.5)},
      {"|-2|", new IntValue(2)},
      {"|-2.5|", new DoubleValue(2.5)},
      {"5!", new IntValue(120)},
      {"(1.0 + 2.5)*2", new DoubleValue(7.0)},
      {"(1 + 2)*5", new IntValue(15)},
    };
  }

  @Test(dataProvider = "arithmeticTestData")
  void evaluateArithmetic_actualEqualsExpected(String input, Value expected) {
    assertEquals(evaluator.evaluate(input), expected);
  }

  @DataProvider
  public Object[][] variableAssignments() {
    return new Object[][] {
      {"y_1=1\nx_1 = y_1 + ( | 5!/10 - (+ 2) ^ 3 ^ 2 + -4*5 + .5 | )", new DoubleValue(520.5)},
      {"a=10\nb   = 3\nc=  - 2\n a+b+c", new IntValue(11)},
      {"x=4\nx", new IntValue(4)},
    };
  }

  @Test(dataProvider = "variableAssignments")
  void evaluateVariableAssignments_actualEqualsExpected(String input, Value expected) {
    assertEquals(evaluator.evaluate(input), expected);
  }

  @Test
  void evaluateUndefined_throwsException() {
    assertThrows(IllegalStateException.class, () -> evaluator.evaluate("a"));
  }

  @Test
  void evaluateDivisionByZero_throwsException() {
    assertThrows(ArithmeticException.class, () -> evaluator.evaluate("1/0"));
  }
}
