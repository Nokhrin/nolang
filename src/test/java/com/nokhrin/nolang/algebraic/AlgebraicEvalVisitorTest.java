package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicLexer;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.values.DoubleValue;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.Value;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class AlgebraicEvalVisitorTest {

    private Value evaluate(String input) {
        Scope scope = new Scope(null);
        AlgebraicLexer lexer = new AlgebraicLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AlgebraicParser parser = new AlgebraicParser(tokens);
        AlgebraicParser.ProgContext tree = parser.prog();
        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor(scope);

        return visitor.visit(tree);
    }

    @DataProvider
    public Object[][] arithmeticTestData() {
        return new Object[][]{
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
        assertEquals(evaluate(input), expected);
    }

    @DataProvider
    public Object[][] variableAssignments() {
        return new Object[][]{
                {"x=4\nx", new IntValue(4)},
                {"a=10\nb   = 3\nc=  - 2\n a+b+c", new IntValue(11)},
        };
    }

    @Test(dataProvider = "variableAssignments")
    void evaluateVariableAssignments_actualEqualsExpected(String input, Value expected) {
        assertEquals(evaluate(input), expected);
    }

    @Test
    void evaluateUndefined_throwsException(){
        assertThrows(IllegalStateException.class,
                ()->evaluate("a"));
    }

    @Test
    void evaluateDivisionByZero_throwsException(){
        assertThrows(ArithmeticException.class,
                ()->evaluate("1/0"));
    }

}