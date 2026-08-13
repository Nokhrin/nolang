package com.nokhrin.nolang.common;

import com.nokhrin.nolang.common.operations.Arithmetic;
import com.nokhrin.nolang.common.values.DoubleValue;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.Value;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ArithmeticTest {

    @DataProvider(name = "additionCases")
    public Object[][] provideAdditionCases() {
        return new Object[][]{
                {"int + int", new IntValue(5), new IntValue(3), new IntValue(8)},
                {"int + int negative", new IntValue(-5), new IntValue(3), new IntValue(-2)},
                {"int + int zero", new IntValue(0), new IntValue(0), new IntValue(0)},
                {"int + double", new IntValue(5), new DoubleValue(3.5), new DoubleValue(8.5)},
                {"double + int", new DoubleValue(5.5), new IntValue(3), new DoubleValue(8.5)},
                {"double + double", new DoubleValue(5.5), new DoubleValue(3.5), new DoubleValue(9.0)},
        };
    }

    @Test(dataProvider = "additionCases")
    public void add_validOperands_expectedValue(String description, Value left, Value right, Value expected) {
        assertEquals(Arithmetic.add(left, right), expected, description);
    }

    @DataProvider(name = "divisionCases")
    public Object[][] provideDivisionCases() {
        return new Object[][]{
                {"int / int exact", new IntValue(10), new IntValue(2), new IntValue(5)},
                {"int / int fractional", new IntValue(5), new IntValue(2), new DoubleValue(2.5)},
                {"double / int", new DoubleValue(5.5), new IntValue(2), new DoubleValue(2.75)},
                {"int / 1", new IntValue(42), new IntValue(1), new IntValue(42)},
                {"int / -1", new IntValue(42), new IntValue(-1), new IntValue(-42)},
        };
    }

    @Test(dataProvider = "divisionCases")
    public void div_validOperands_expectedValue(String description, Value left, Value right, Value expected) {
        assertEquals(Arithmetic.div(left, right), expected, description);
    }

    @DataProvider(name = "negationCases")
    public Object[][] provideNegationCases() {
        return new Object[][]{
                {"int positive", new IntValue(5), new IntValue(-5)},
                {"int negative", new IntValue(-5), new IntValue(5)},
                {"int zero", new IntValue(0), new IntValue(0)},
                {"double positive", new DoubleValue(5.5), new DoubleValue(-5.5)},
                {"double negative", new DoubleValue(-5.5), new DoubleValue(5.5)},
        };
    }

    @Test(dataProvider = "negationCases")
    public void neg_validOperand_expectedValue(String description, Value operand, Value expected) {
        assertEquals(Arithmetic.neg(operand), expected, description);
    }

    @Test
    void div_byZero_throwsArithmeticException() {
        assertThrows(ArithmeticException.class,
                () -> Arithmetic.div(new IntValue(1), new IntValue(0)));
    }

    @Test(enabled = false, description = "will be implemented in sprint 3")
    void add_intMaxPlusOne_returnsDouble(){
        Value result = Arithmetic.add(
                new IntValue(Integer.MAX_VALUE),
                new IntValue(1)
        );
        assertTrue(result instanceof DoubleValue);
    }
}