package com.nokhrin.nolang.common;

import static com.nokhrin.nolang.common.operations.Logical.*;
import static com.nokhrin.nolang.common.operations.Logical.LogicalOperation.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.RealValue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LogicalExamplesTest {

  @DataProvider
  public Object[][] andExpressions() {
    return new Object[][] {
      {new BoolValue(true), new BoolValue(true), new BoolValue(true)},
      {new BoolValue(true), new BoolValue(false), new BoolValue(false)},
      {new BoolValue(false), new BoolValue(true), new BoolValue(false)},
      {new BoolValue(false), new BoolValue(false), new BoolValue(false)},
    };
  }

  @Test(dataProvider = "andExpressions")
  public void and_validOperands_expectedResult(
      BoolValue left, BoolValue right, BoolValue expected) {
    assertEquals(and(left, right), expected);
  }

  @DataProvider
  public Object[][] orExpressions() {
    return new Object[][] {
      {new BoolValue(true), new BoolValue(true), new BoolValue(true)},
      {new BoolValue(true), new BoolValue(false), new BoolValue(true)},
      {new BoolValue(false), new BoolValue(true), new BoolValue(true)},
      {new BoolValue(false), new BoolValue(false), new BoolValue(false)},
    };
  }

  @Test(dataProvider = "orExpressions")
  public void or_validOperands_expectedResult(BoolValue left, BoolValue right, BoolValue expected) {
    assertEquals(or(left, right), expected);
  }

  @Test
  public void and_nonBoolLeft_throwsIllegalStateException() {
    assertThrows(IllegalStateException.class, () -> and(new IntValue(5), new BoolValue(true)));
  }

  @Test
  public void or_nonBoolRight_throwsIllegalStateException() {
    assertThrows(IllegalStateException.class, () -> or(new BoolValue(true), new IntValue(5)));
  }

  @DataProvider
  public Object[][] notExpressions() {
    return new Object[][] {
      {new BoolValue(true), new BoolValue(false)},
      {new BoolValue(false), new BoolValue(true)},
    };
  }

  @Test(dataProvider = "notExpressions")
  public void not_validOperand_expectedResult(BoolValue operand, BoolValue expected) {
    assertEquals(not(operand), expected);
  }

  @Test
  public void not_nonBool_throwsIllegalStateException() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          not(new IntValue(5));
        });
  }

  @DataProvider
  public Object[][] comparisonExpressions() {
    return new Object[][] {
      {new IntValue(5), GT, new IntValue(3), new BoolValue(true)},
      {new IntValue(3), GT, new IntValue(5), new BoolValue(false)},
      {new IntValue(5), EQ, new IntValue(5), new BoolValue(true)},
      {new IntValue(5), NEQ, new IntValue(3), new BoolValue(true)},
      {new IntValue(5), GEQ, new IntValue(5), new BoolValue(true)},
      {new IntValue(5), LEQ, new IntValue(3), new BoolValue(false)},
      {new RealValue(5.5), GT, new RealValue(3.5), new BoolValue(true)},
      {new IntValue(5), GT, new RealValue(3.5), new BoolValue(true)},
    };
  }

  @Test(dataProvider = "comparisonExpressions")
  public void compare_validOperands_expectedResult(
      NumericValue left, LogicalOperation op, NumericValue right, BoolValue expected) {
    assertEquals(compare(left, op, right), expected);
  }
}
