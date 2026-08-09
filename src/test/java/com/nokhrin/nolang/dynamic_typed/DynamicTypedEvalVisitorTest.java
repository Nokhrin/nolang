package com.nokhrin.nolang.dynamic_typed;

import com.nokhrin.nolang.DynamicTypedLexer;
import com.nokhrin.nolang.DynamicTypedParser;
import com.nokhrin.nolang.common.executions.FunctionRegistry;
import com.nokhrin.nolang.common.executions.Result;
import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.executions.VoidResult;
import com.nokhrin.nolang.common.values.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.testng.Assert.*;

public class DynamicTypedEvalVisitorTest {

    private Result eval(String input) {
        Scope globals = new Scope(null);
        FunctionRegistry functionsRegistry = new FunctionRegistry(System.out);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream testOutput = new PrintStream(outputStream);

        var lexer = new DynamicTypedLexer(CharStreams.fromString(input));
        var tokens = new CommonTokenStream(lexer);
        var parser = new DynamicTypedParser(tokens);
        ParseTree tree = parser.prog();
        var visitor = new DynamicTypedEvalVisitor(globals, functionsRegistry);
        return visitor.visit(tree);
    }

    @DataProvider
    public Object[][] evaluateArithmetic_actualEqualsExpected() {
        return new Object[][] {
                {"5 + 3\n", new IntValue(8)},
                {"10 - 2\n", new IntValue(8)},
                {"4 * 5\n", new IntValue(20)},
                {"20 / 4\n", new IntValue(5)},
                {"2 + 3 * 4\n", new IntValue(14)},
                {"(2 + 3) * 4\n", new IntValue(20)},
                {"-5 + 3\n", new IntValue(-2)},
                {"+10 - 5\n", new IntValue(5)},
                {"3.14 + 2.86\n", new DoubleValue(6.0)},
                {"1.234e-3 + 0.001\n", new DoubleValue(0.002234)},
                {".5 * 2\n", new DoubleValue(1.0)},
                {"1E2 + 50\n", new DoubleValue(150.0)},
                {"1e2 + 50\n", new DoubleValue(150.0)},
                {"1 + 2 - 3\n", new IntValue(0)},
                {"10 / 2 * 3\n", new IntValue(15)},
                {"10 - 2 - 3\n", new IntValue(5)},
                {"(1 + 2) * 3\n", new IntValue(9)},
        };
    }

    @Test(dataProvider = "evaluateArithmetic_actualEqualsExpected")
    public void evaluateArithmetic_actualEqualsExpected(String input, Value expected) {
        assertEquals(eval(input), expected);
    }

    @DataProvider
    public Object[][] evaluateLogic_actualEqualsExpected() {
        return new Object[][] {
                {"true AND false\n", new BoolValue(false)},
                {"true OR false\n", new BoolValue(true)},
                {"NOT true\n", new BoolValue(false)},
                {"NOT false\n", new BoolValue(true)},
                {"true AND true\n", new BoolValue(true)},
                {"false OR false\n", new BoolValue(false)},
                {"NOT (true AND false)\n", new BoolValue(true)},
                {"NOT (false OR false)\n", new BoolValue(true)},
                {"true AND true AND false\n", new BoolValue(false)},
                {"false OR false OR true\n", new BoolValue(true)},
        };
    }

    @Test(dataProvider = "evaluateLogic_actualEqualsExpected")
    public void evaluateLogic_actualEqualsExpected(String input, Value expected) {
        assertEquals(eval(input), expected);
    }

    @DataProvider
    public Object[][] evaluateComparison_actualEqualsExpected() {
        return new Object[][] {
                {"5 > 3\n", new BoolValue(true)},
                {"3 > 5\n", new BoolValue(false)},
                {"10 == 10\n", new BoolValue(true)},
                {"3 != 4\n", new BoolValue(true)},
                {"5 >= 5\n", new BoolValue(true)},
                {"2 <= 3\n", new BoolValue(true)},
                {"5 < 3\n", new BoolValue(false)},
                {"10 != 10\n", new BoolValue(false)},
                {"5 >= 6\n", new BoolValue(false)},
                {"3 <= 2\n", new BoolValue(false)},
                {"5.5 > 3.5\n", new BoolValue(true)},
                {"5 > 3.5\n", new BoolValue(true)},
                {"3.5 < 5\n", new BoolValue(true)},
        };
    }

    @Test(dataProvider = "evaluateComparison_actualEqualsExpected")
    public void evaluateComparison_actualEqualsExpected(String input, Value expected) {
        assertEquals(eval(input), expected);
    }

    @DataProvider
    public Object[][] evaluateAssignment_actualEqualsExpected() {
        return new Object[][] {
                {"x = 5\nx\n", new IntValue(5)},
                {"y = 3.14\ny\n", new DoubleValue(3.14)},
                {"z = true\nz\n", new BoolValue(true)},
                {"x = 5\ny = 3\nx + y\n", new IntValue(8)},
                {"x = 5\ny = 3.14\nx + y\n", new DoubleValue(8.14)},
                {"x = 10\ny = 2\nx / y\n", new IntValue(5)},
                {"x = 5\nx = 10\nx\n", new IntValue(10)},
                {"x = 2\ny = 3\nz = x + y\nz\n", new IntValue(5)},
                {"a = 5\nb = 3\na > b\n", new BoolValue(true)},
                {"x = 5\ny = 3\nx + y > 7\n", new BoolValue(true)},
        };
    }

    @Test(dataProvider = "evaluateAssignment_actualEqualsExpected")
    public void evaluateAssignment_actualEqualsExpected(String input, Value expected) {
        assertEquals(eval(input), expected);
    }

    @DataProvider
    public Object[][] evaluateIfElse_actualEqualsExpected() {
        return new Object[][] {
                {"x = 5\nif x > 0 then y = 1\ny\n", new IntValue(1)},
                {"x = 5\nif x > 0 then y = 1 else y = 0\ny\n", new IntValue(1)},
                {"x = -1\nif x > 0 then y = 1 else y = 0\ny\n", new IntValue(0)},
                {"x = 5\nif x > 0 then if x > 10 then y = 2 else y = 1\ny\n", new IntValue(1)},
                {"x = 5\ny = 3\nif x > 0 AND y > 0 then z = true else z = false\nz\n", new BoolValue(true)},
        };
    }

    @Test(dataProvider = "evaluateIfElse_actualEqualsExpected")
    public void evaluateIfElse_actualEqualsExpected(String input, Value expected) {
        assertEquals(eval(input), expected);
    }

    @Test
    public void evaluateIfElse_yNotDefined_throwsIllegalArgExc() {
        String input = "x = -1\nif x > 0 then y = 1\ny";
        assertThrows(IllegalArgumentException.class,
                ()->eval(input));
    }

    @Test
    void visitIfStat_nonBoolCondition_throwsExc(){
        String input = "x = -1\nif 0 then y = 1\ny";
        assertThrows(IllegalArgumentException.class,
                ()->eval(input));
    }

    @Test
    void visitIfStat_falseConditionNoElse_returnsVoid(){
        String input = "x = -1\nif x > 0 then y = 1";
        Result result = eval(input);
        assertEquals(result, new VoidResult());
    }

    @DataProvider
    public Object[][] validBuiltInFunctionCalls() {
        return new Object[][] {
                {"print(5)\n", new VoidResult()},
                {"sin(0)\n", new DoubleValue(0.0)},
                {"abs(-10)\n", new IntValue(10)},
                {"abs(-3.14)\n", new DoubleValue(3.14)},
                {"pow(2, 2)\n", new IntValue(4)},
                {"pow(9, 0.5)\n", new DoubleValue(3.0)},
                {"pow(1, -1)\n", new IntValue(1)},
        };
    }

    @Test(dataProvider = "validBuiltInFunctionCalls")
    public void evaluateBuiltInFunctions_actualEqualsExpected(String input, Result expected) {
        assertEquals(eval(input), expected);
    }

    @Test
    void call_unknownFunction_throwsException(){
        String input = "unknown(123)\n";
        assertThrows(IllegalArgumentException.class,
                ()->eval(input));
    }

    @Test
    void call_funcValidCountOfArgsUnexpected_throwsException(){
        String input = "pow(1,2,3)\n";
        assertThrows(IllegalArgumentException.class,
                ()->eval(input));
    }
    @DataProvider
    public Object[][] userDefinedFunctionCalls(){
        return new Object[][]{
        {
            """
        def add(a,b) { a + b }
        add(2,3)
        """, new IntValue(5)},
        {
            """
        def isPositive(x) {
            if x >= 0 then { return true }
        }
        isPositive(1)
        """, new BoolValue(true)},
        {
            """
        def square(x) { x * x }
        def add(a, b) { a + b }
        square(add(1,2))
        """, new IntValue(9)}
    };
    }

    @Test(dataProvider = "userDefinedFunctionCalls")
    void visitFuncCall_userDefinedValid_resultCorrect(String input, Value expected){
        assertEquals(eval(input), expected);
    }

    @DataProvider
    public Object[][] userDefinedRecursiveFunctionCalls(){
        return new Object[][]{
        {
                        """
        def factorial(x) {
            if x <= 1 then { return 1 }
            return x * factorial(x - 1)
        }
        factorial(5)
        """, new IntValue(120)},
        {
                        """
        def fibonacci(x) {
            if x <= 0 then { return 0 }
            if x == 1 then { return 1 }
            return fibonacci(x-1) + fibonacci(x - 2)
        }
        fibonacci(8)
        """, new IntValue(21)},
        {
                        """
        def sum_to(x) {
            if x <= 0 then { return 0 }
            return x + sum_to(x-1)
        }
        sum_to(10)
        """, new IntValue(55)},
        };
    }

    @Test(dataProvider = "userDefinedRecursiveFunctionCalls")
    void visitFuncCall_userDefinedRecursiveValid_resultCorrect(String input, Value expected){
        assertEquals(eval(input), expected);
    }

    @DataProvider
    public Object[][] validTernary() {
        return new Object[][] {
                {"5 > 3 ? 10 : 20\n", new IntValue(10)},
                {"5+1 > 10-4 ? 10 : 2*10\n", new IntValue(20)},
        };
    }

    @Test(dataProvider = "validTernary")
    public void evaluateTernary_actualEqualsExpected(String input, Value expected) {
        assertEquals(eval(input), expected);
    }
}

