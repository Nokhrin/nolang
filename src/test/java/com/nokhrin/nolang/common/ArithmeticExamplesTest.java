package com.nokhrin.nolang.common;

import static org.testng.Assert.*;

import com.nokhrin.nolang.common.operations.Arithmetic;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.RealValue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ArithmeticExamplesTest {

  @DataProvider(name = "additionCases")
  public Object[][] provideAdditionCases() {
    return new Object[][] {
      {new IntValue(5), new IntValue(3), new IntValue(8)},
      {new IntValue(-5), new IntValue(3), new IntValue(-2)},
      {new IntValue(0), new IntValue(0), new IntValue(0)},
      {new IntValue(5), new RealValue(3.5), new RealValue(8.5)},
      {new RealValue(5.5), new IntValue(3), new RealValue(8.5)},
      {new RealValue(5.5), new RealValue(3.5), new RealValue(9.0)},
    };
  }

  @Test(dataProvider = "additionCases")
  public void add_validOperands_expectedValue(
      NumericValue left, NumericValue right, NumericValue expected) {
    assertEquals(Arithmetic.add(left, right), expected);
  }

  @DataProvider(name = "divisionCases")
  public Object[][] provideDivisionCases() {
    return new Object[][] {
      {new IntValue(10), new IntValue(2), new IntValue(5)},
      {new IntValue(5), new IntValue(2), new IntValue(2)},
      {new RealValue(5), new IntValue(2), new RealValue(2.5)},
      {new RealValue(5.5), new IntValue(2), new RealValue(2.75)},
      {new IntValue(42), new IntValue(1), new IntValue(42)},
      {new IntValue(42), new IntValue(-1), new IntValue(-42)},
    };
  }

  @Test(dataProvider = "divisionCases")
  public void div_validOperands_expectedValue(
      NumericValue left, NumericValue right, NumericValue expected) {
    assertEquals(Arithmetic.div(left, right), expected);
  }

  @DataProvider(name = "negationCases")
  public Object[][] provideNegationCases() {
    return new Object[][] {
      {new IntValue(5), new IntValue(-5)},
      {new IntValue(-5), new IntValue(5)},
      {new IntValue(0), new IntValue(0)},
      {new RealValue(5.5), new RealValue(-5.5)},
      {new RealValue(-5.5), new RealValue(5.5)},
    };
  }

  @Test(dataProvider = "negationCases")
  public void neg_validOperand_expectedValue(NumericValue operand, NumericValue expected) {
    assertEquals(Arithmetic.neg(operand), expected);
  }

  @Test
  void div_byZero_throwsArithmeticException() {
    assertThrows(ArithmeticException.class, () -> Arithmetic.div(new IntValue(1), new IntValue(0)));
  }

  @Test(enabled = false, description = "will be implemented in sprint 3")
  void add_intMaxPlusOne_returnsDouble() {
    NumericValue result = Arithmetic.add(new IntValue(Integer.MAX_VALUE), new IntValue(1));
    assertTrue(result instanceof RealValue);
  }
}
