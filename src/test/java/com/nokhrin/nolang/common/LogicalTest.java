package com.nokhrin.nolang.common;

import com.nokhrin.nolang.common.operations.Logical;
import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.DoubleValue;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.values.IntValue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class LogicalTest {

    @DataProvider
    public Object[][] andTestData() {
        return new Object[][] {
                {"true AND true", new BoolValue(true), new BoolValue(true), new BoolValue(true)},
                {"true AND false", new BoolValue(true), new BoolValue(false), new BoolValue(false)},
                {"false AND true", new BoolValue(false), new BoolValue(true), new BoolValue(false)},
                {"false AND false", new BoolValue(false), new BoolValue(false), new BoolValue(false)},
        };
    }

    @Test(dataProvider = "andTestData")
    public void and_validOperands_expectedResult(String description, Value left, Value right, Value expected) {
        assertEquals(Logical.and(left, right), expected, description);
    }

    @DataProvider
    public Object[][] orTestData() {
        return new Object[][] {
                {"true OR true", new BoolValue(true), new BoolValue(true), new BoolValue(true)},
                {"true OR false", new BoolValue(true), new BoolValue(false), new BoolValue(true)},
                {"false OR true", new BoolValue(false), new BoolValue(true), new BoolValue(true)},
                {"false OR false", new BoolValue(false), new BoolValue(false), new BoolValue(false)},
        };
    }

    @Test(dataProvider = "orTestData")
    public void or_validOperands_expectedResult(String description, Value left, Value right, Value expected) {
        assertEquals(Logical.or(left, right), expected, description);
    }

    @Test
    public void and_nonBoolLeft_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> {
            Logical.and(new IntValue(5), new BoolValue(true));
        });
    }

    @Test
    public void or_nonBoolRight_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> {
            Logical.or(new BoolValue(true), new IntValue(5));
        });
    }

    @DataProvider
    public Object[][] notTestData() {
        return new Object[][] {
                {"NOT true", new BoolValue(true), new BoolValue(false)},
                {"NOT false", new BoolValue(false), new BoolValue(true)},
        };
    }

    @Test(dataProvider = "notTestData")
    public void not_validOperand_expectedResult(String description, Value operand, Value expected) {
        assertEquals(Logical.not(operand), expected, description);
    }

    @Test
    public void not_nonBool_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> {
            Logical.not(new IntValue(5));
        });
    }

    @DataProvider
    public Object[][] comparisonTestData() {
        return new Object[][] {
                {"5 > 3", new IntValue(5), ">", new IntValue(3), new BoolValue(true)},
                {"3 > 5", new IntValue(3), ">", new IntValue(5), new BoolValue(false)},
                {"5 == 5", new IntValue(5), "==", new IntValue(5), new BoolValue(true)},
                {"5 != 3", new IntValue(5), "!=", new IntValue(3), new BoolValue(true)},
                {"5 >= 5", new IntValue(5), ">=", new IntValue(5), new BoolValue(true)},
                {"5 <= 3", new IntValue(5), "<=", new IntValue(3), new BoolValue(false)},
                {"5.5 > 3.5", new DoubleValue(5.5), ">", new DoubleValue(3.5), new BoolValue(true)},
                {"5 > 3.5", new IntValue(5), ">", new DoubleValue(3.5), new BoolValue(true)},
        };
    }

    @Test(dataProvider = "comparisonTestData")
    public void compare_validOperands_expectedResult(String description, Value left, String op, Value right, Value expected) {
        assertEquals(Logical.compare(left, op, right), expected, description);
    }
}