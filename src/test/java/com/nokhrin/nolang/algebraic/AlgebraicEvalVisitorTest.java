package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.exceptions.SemanticException;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.RealValue;
import com.nokhrin.nolang.common.values.Value;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.testng.Assert.assertThrows;

public class AlgebraicEvalVisitorTest {
    AlgebraicInterpreter interpreter;

    @BeforeMethod
    private void setUp() {
        PrintStream output = new PrintStream(OutputStream.nullOutputStream());
        interpreter = new AlgebraicInterpreter(output);
    }

    @DataProvider
    public Object[][] arithmeticTestData() {
        return new Object[][]{
            {"1+2", new IntValue(3)},
            {"10 -   3  - 2 ", new IntValue(5)},
            {"2 * 3 + 4", new IntValue(10)},
            {"10 / 2 * 3", new IntValue(15)},
            {"1.0 + 2.5", new RealValue(3.5)},
            {"2 ^ 5", new IntValue(32)},
            {"2.0 ^ 2.0", new IntValue(4)},
            {"2.5 ^ 1.0", new RealValue(2.5)},
            {"|-2|", new IntValue(2)},
            {"|-2.5|", new RealValue(2.5)},
            {"5!", new IntValue(120)},
            {"(1.0 + 2.5)*2", new RealValue(7.0)},
            {"(1 + 2)*5", new IntValue(15)},
        };
    }

    @Test(dataProvider = "arithmeticTestData")
    void evaluateArithmetic_actualEqualsExpected(String input, Value expected) {
        assertEquals(interpreter.evaluate(input), expected);
    }

    @DataProvider
    public Object[][] variableAssignments() {
        return new Object[][]{
            {"y_1=1\nx_1 = y_1 + ( | 5!/10 - (+ 2) ^ 3 ^ 2 + -4*5 + .5 | )", new RealValue(520.5)},
            {"a=10\nb   = 3\nc=  - 2\n a+b+c", new IntValue(11)},
            {"x=4\nx", new IntValue(4)},
        };
    }

    @Test(dataProvider = "variableAssignments")
    void evaluateVariableAssignments_actualEqualsExpected(String input, Value expected) {
        assertEquals(interpreter.evaluate(input), expected);
    }

    @Test
    void evaluateUndefined_throwsException() {
        assertThrows(SemanticException.class, () -> interpreter.evaluate("a"));
    }

    @Test
    void evaluateDivisionByZero_throwsException() {
        assertThrows(ArithmeticException.class, () -> interpreter.evaluate("1/0"));
    }
}
