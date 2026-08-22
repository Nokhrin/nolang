package com.nokhrin.nolang.algebraic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import com.nokhrin.nolang.common.executions.Result;
import com.nokhrin.nolang.common.executions.ValueResult;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.RealValue;
import java.io.PrintStream;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AlgebraicEvalVisitorTest {
  AlgebraicInterpreter interpreter;

  @BeforeMethod
  private void setUp() {
    PrintStream output = new PrintStream(System.out);
    interpreter = new AlgebraicInterpreter(output);
  }

  @DataProvider
  public Object[][] arithmeticTestData() {
    return new Object[][] {
      {"1+2", new ValueResult(new IntValue(3))},
      {"10 -   3  - 2 ", new ValueResult(new IntValue(5))},
      {"2 * 3 + 4", new ValueResult(new IntValue(10))},
      {"10 / 2 * 3", new ValueResult(new IntValue(15))},
      {"1.0 + 2.5", new ValueResult(new RealValue(3.5))},
      {"2 ^ 5", new ValueResult(new IntValue(32))},
      {"2.0 ^ 2.0", new ValueResult(new RealValue(4))},
      {"2.5 ^ 1.0", new ValueResult(new RealValue(2.5))},
      {"|-2|", new ValueResult(new IntValue(2))},
      {"|-2.5|", new ValueResult(new RealValue(2.5))},
      {"5!", new ValueResult(new IntValue(120))},
      {"(1.0 + 2.5)*2", new ValueResult(new RealValue(7.0))},
      {"(1 + 2)*5", new ValueResult(new IntValue(15))},
    };
  }

  @Test(dataProvider = "arithmeticTestData")
  void evaluateArithmetic_actualEqualsExpected(String input, Result expected) {
    assertEquals(interpreter.evaluate(input), expected);
  }

  @DataProvider
  public Object[][] variableAssignments() {
    return new Object[][] {
      {
        "y_1=1\nx_1 = y_1 + ( | 5!/10 - (+ 2) ^ 3 ^ 2 + -4*5 + .5 | )\nx_1",
        new ValueResult(new RealValue(520.5))
      },
      {"a=10\nb   = 3\nc=  - 2\n a+b+c", new ValueResult(new IntValue(11))},
      {"x=4\nx", new ValueResult(new IntValue(4))},
    };
  }

  @Test(dataProvider = "variableAssignments")
  void evaluateVariableAssignments_actualEqualsExpected(String input, Result expected) {
    assertEquals(interpreter.evaluate(input), expected);
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
