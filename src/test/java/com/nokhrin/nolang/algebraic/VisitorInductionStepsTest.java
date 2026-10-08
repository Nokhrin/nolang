package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class VisitorInductionStepsTest extends AlgebraicIntegrationTestBase {

    private static Stream<Arguments> arithmeticExpression() {
        return Stream.of(
            Arguments.of("""
                8
                """, new NumericValue.IntValue(8)),
            Arguments.of("""
                1+2*3
                """, new NumericValue.IntValue(7)),
            Arguments.of("""
                1*2*3
                """, new NumericValue.IntValue(6)),
            Arguments.of("""
                1*2+3
                """, new NumericValue.IntValue(5)),
            Arguments.of("""
                9-2-3
                """, new NumericValue.IntValue(4))
        );
    }

    /**
     * example: visiting 1+2*3
     * term->factor->unary->postfix->atom
     * ProgramContext
     * -> cast to ProgramWithStatementsContext
     *  -> call ProgramWithStatementsContext#statement()
     *  -> StatementContext
     *   -> delegate AssignmentContext
     *    -> cast to TermStatementContext
     *     -> call TermStatementContext#term()
     *     -> TermContext
     *      -> call com.nokhrin.nolang.AlgebraicParser.TermContext#factor()
     *       -> FactorContext ->unary->postfix->atom->NUM
     *      -> call com.nokhrin.nolang.AlgebraicParser.TermContext#addOp()
     *      -> call com.nokhrin.nolang.AlgebraicParser.TermContext#factor()
     *       -> com.nokhrin.nolang.AlgebraicParser.FactorContext#unary()
     *        -> com.nokhrin.nolang.AlgebraicParser.UnaryContext
     *       -> com.nokhrin.nolang.AlgebraicParser.FactorContext#mulOp()
     *        -> com.nokhrin.nolang.AlgebraicParser.MulOpContext
     *       -> com.nokhrin.nolang.AlgebraicParser.FactorContext#unary()
     *        -> com.nokhrin.nolang.AlgebraicParser.UnaryContext
     */
    @ParameterizedTest
    @MethodSource("arithmeticExpression")
    void expressionEvaluation_scopeNotModified_numericValueReturned(String src, Value expected) {
        Scope scopeInitial = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scopeInitial, registry, List.of());

        AlgebraicParser.TermContext termContext = extractTerm(src);

        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> termEval = visitor.visitTerm(termContext);

        EvalResult<Value> result = termEval.run(executionContext);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(expected, actual.value()),
                () -> assertEquals(scopeInitial, actual.executionContext().scope())
            );

            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interrupt: " + interrupted.reason());
        }
    }

    @Test
    void readDefinedNumericVar_scopeNotModified_numericValueReturned() {
        String varName = "x";
        NumericValue.IntValue valExpected = new NumericValue.IntValue(42);

        Scope scope = new Scope()
            .define(varName, valExpected)
            .rightOptional().orElseThrow();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext context = new ExecutionContext(scope, registry, List.of());

        String src = """
            x
            """;
        AlgebraicParser.TermContext termContext = extractTerm(src);

        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> termEval = visitor.visitTerm(termContext);

        EvalResult<Value> result = termEval.run(context);
        switch (result) {
            case EvalResult.Returned<Value> returned -> assertAll(
                () -> assertTrue(returned.executionContext().scope().bindings().containsKey("x")),
                () -> assertEquals(Either.right(valExpected), returned.executionContext().scope().lookup("x")),
                () -> assertEquals(valExpected, returned.value())
            );
            case EvalResult.Interrupted<Value> interrupted ->
                fail("Unexpected interruption: " + interrupted.reason().message());
        }
    }

    /**
     * ProgramContext
     * -> cast to ProgramWithStatementsContext
     *  -> call .statement(0)
     *  -> return StatementContext
     *   -> call AssignmentContext
     *    -> choose AssignStatementContext
     *    or
     *    -> choose TermStatementContext
     *     -> call .term()
     *
     * @param src
     * @return
     */
    private AlgebraicParser.TermContext extractTerm(String src) {
        var program = (AlgebraicParser.ProgramWithStatementsContext) AlgebraicSyntaxAnalyzer.parse(src).rightOptional().orElseThrow();
        var stmnt = program.statement(0);
        var assignParent = stmnt.assignment();

        var term = switch (assignParent) {
            case AlgebraicParser.AssignStatementContext assignStmnt -> assignStmnt.term();
            case AlgebraicParser.TermStatementContext termStmnt -> termStmnt.term();
            default -> throw new IllegalStateException("Unexpected value: " + assignParent);
        };
        return term;
    }

    @Test
    void boolVal_interrupted_typeErrorReturned() {
        String src = """
            1 AND true
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);
        switch (result) {
            case EvalResult.Interrupted<Value> actual -> assertAll(
                () -> assertInstanceOf(InterruptReason.Error.class, actual.reason()),
//                ()->assertInstanceOf(EvalError.TypeError.class, actual.reason().cause()),
                () -> assertTrue(actual.executionContext().scope().bindings().isEmpty())
            );
            case EvalResult.Returned<Value> returned -> fail("Unexpected return: " + returned);
        }
    }

    @Test
    void calculationError_interrupted_ArithmeticErrorReturned() {
        String src = """
            1/0
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Interrupted<Value> actual -> assertAll(
                () -> assertInstanceOf(InterruptReason.Error.class, actual.reason()),
//                ()->assertInstanceOf(EvalError.ArithmeticError.class, actual.reason().cause()),
                () -> assertFalse(actual.executionContext().scope().isDefined("undefinedVar"))
            );
            case EvalResult.Returned<Value> returned -> fail("Unexpected return: " + returned);
        }
    }


    @Test
    void defineRedefineRead_redefinedReturned() {
        String src = """
            x=1
            x=2
            x
            """;
        NumericValue.IntValue expected = new NumericValue.IntValue(2);
        EvalResult<Value> result = interpreter.evaluate(src, context);
        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(expected, actual.value()),
                () -> assertEquals(
                    Either.right(expected),
                    actual.executionContext().scope().lookup("x")
                )
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }
    }


    @Test
    void varDefined_scopeWithVar_voidReturned() {
        String src = """
            x=1
            """;
        NumericValue.IntValue expected = new NumericValue.IntValue(1);
        EvalResult<Value> result = interpreter.evaluate(src, context);
        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(Value.VoidValue.INSTANCE, actual.value()),
                () -> assertEquals(
                    Either.right(expected),
                    actual.executionContext().scope().lookup("x")
                )
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }
    }

    @Test
    void varRedefined_scopeWithUpdatedVar_voidReturned() {
        String src = """
            x=1
            x=2
            """;

        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(1, actual.executionContext().scope().bindings().size()),
                () -> assertEquals(
                    Either.right(new NumericValue.IntValue(2)),
                    actual.executionContext().scope().lookup("x")
                ),
                () -> assertEquals(Value.VoidValue.INSTANCE, actual.value())
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }

    }

    @Test
    void malformedDefinition_scopeWithoutVar_errorReturned() {
        String src = """
            x=1/0
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Interrupted<Value> actual -> assertAll(
                () -> assertEquals(0, actual.executionContext().scope().bindings().size()),
//                ()->assertInstanceOf(EvalError.ArithmeticError.class, actual.reason().cause()),
                () -> assertInstanceOf(InterruptReason.Error.class, actual.reason())
            );
            case EvalResult.Returned<Value> returned -> fail("Unexpected return: " + returned);
        }

    }

    @Test
    void defineWithVarNotInScope_scopeWithoutVar_errorReturned() {
        String src = """
            x=undefined_var
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Interrupted<Value> actual -> assertAll(
                () -> assertEquals(0, actual.executionContext().scope().bindings().size()),
//                ()->assertInstanceOf(ScopeError.UndefinedVariable.class, actual.reason().cause()),
//                ()->assertEquals("undefined_var", actual.reason().cause().name()),
                () -> assertInstanceOf(InterruptReason.Error.class, actual.reason())
            );
            case EvalResult.Returned<Value> returned -> fail("Unexpected return: " + returned);
        }
    }


    @Test
    void readUndefined_undefinedErrorReturned() {
        String src = """
            undefinedVar
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);
        switch (result) {
            case EvalResult.Interrupted<Value> actual -> assertAll(
//                ()->assertInstanceOf(ScopeError.UndefinedVariable.class, actual.reason().cause()),
//                ()->assertEquals("undefined_var", actual.reason().cause().name()),
                () -> assertInstanceOf(InterruptReason.Error.class, actual.reason())
            );
            case EvalResult.Returned<Value> returned -> fail("Unexpected return: " + returned);
        }
    }


    @Test
    void calculate_lastResultReturned_scopeWithoutVarsCreated() {
        String src = """
            1+2
            8
            """;

        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(new NumericValue.IntValue(8), actual.value()),
                () -> assertEquals(0, actual.executionContext().scope().bindings().size())
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }
    }

    @Test
    void calculateAssignReassign_voidReturned_scopeWithVarCreated() {
        String src = """
            x=1+2
            x=8
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(Value.VoidValue.INSTANCE, actual.value()),
                () -> assertEquals(new NumericValue.IntValue(8), actual.executionContext().scope().lookup("x").rightOptional().orElseThrow()),
                () -> assertEquals(1, actual.executionContext().scope().bindings().size())
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }
    }

    @Test
    void calculateAndAssign_voidReturned_scopeWithVarCreated() {
        String src = """
            x=1+2
            x+8
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(new NumericValue.IntValue(11), actual.value()),
                () -> assertEquals(new NumericValue.IntValue(3), actual.executionContext().scope().lookup("x").rightOptional().orElseThrow()),
                () -> assertEquals(1, actual.executionContext().scope().bindings().size())
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }
    }

    @Test
    void calculationInterruption_errorReturned_scopeWithFirstVarCreated() {
        String src = """
            x=1
            x/0
            x=2
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Interrupted<Value> actual -> assertAll(
                () -> assertInstanceOf(InterruptReason.Error.class, actual.reason()),
//                ()->assertInstanceOf(EvalError.ArithmeticError.class, actual.reason().cause()),
//                ()->assertInstanceOf("Division by zero", actual.reason().cause().message()),
                () -> assertEquals(new NumericValue.IntValue(1), actual.executionContext().scope().lookup("x").rightOptional().orElseThrow()),
                () -> assertEquals(1, actual.executionContext().scope().bindings().size())
            );
            case EvalResult.Returned<Value> returned -> fail("Unexpected return: " + returned);
        }
    }

    @Test
    void definitionInterrupted_errorReturned_scopeIsEmpty() {
        String src = """
            undefined_var
            x=1
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Interrupted<Value> actual -> assertAll(
                () -> assertInstanceOf(InterruptReason.Error.class, actual.reason()),
//                ()->assertInstanceOf(ScopeError.UndefinedVariable.class, actual.reason().cause()),
//                ()->assertEquals("undefined_var", actual.reason().cause().name()),
                () -> assertEquals(0, actual.executionContext().scope().bindings().size())
            );
            case EvalResult.Returned<Value> returned -> fail("Unexpected return: " + returned);
        }
    }


    @Test
    void parentheses_orderPreserved_scopeNotModified_valueReturned() {
        String src = """
            (1)
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(new NumericValue.IntValue(1), actual.value()),
                () -> assertEquals(0, actual.executionContext().scope().bindings().size())
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }
    }

    @Test
    void absolute_orderPreserved_scopeNotModified_valueReturned() {
        String src = """
            |1|
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(new NumericValue.IntValue(1), actual.value()),
                () -> assertEquals(0, actual.executionContext().scope().bindings().size())
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }
    }

    @Test
    void callBuiltinFunction_scopeNotModified_valueReturned() {
        String src = """
            sin(0)
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(new NumericValue.RealValue(0), actual.value()),
                () -> assertEquals(0, actual.executionContext().scope().bindings().size())
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }
    }


    @Test
    void functionCall_interrupted_typeErrorReturned() {
        String src = """
            abs(true)
            """;
        EvalResult<Value> result = interpreter.evaluate(src, context);
        switch (result) {
            case EvalResult.Interrupted<Value> actual -> assertAll(
                () -> assertInstanceOf(InterruptReason.Error.class, actual.reason()),
                () -> assertFalse(actual.executionContext().scope().isDefined("undefinedVar"))
            );
            case EvalResult.Returned<Value> returned -> fail("Unexpected return: " + returned);
        }
    }

    private static Stream<Arguments> functionExpression() {
        return Stream.of(
            Arguments.of("""
                print(1)
                """, Value.VoidValue.INSTANCE),
            Arguments.of("""
                sin(0)
                """, new NumericValue.RealValue(0))
        );
    }

    @ParameterizedTest
    @MethodSource("functionExpression")
    void callBuiltinFunction_scopeNotModified_voidReturned(String src, Value expected) {
        Scope scopeInitial = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        List<String> stdout = List.of();
        context = new ExecutionContext(scopeInitial, registry, stdout);
        interpreter = AlgebraicInterpreter.create();

        EvalResult<Value> result = interpreter.evaluate(src, context);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(expected, actual.value()),
                () -> assertEquals(scopeInitial, actual.executionContext().scope())
            );
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption");
        }
    }

    private static Stream<Arguments> postfixExpression() {
        return Stream.of(
            Arguments.of("""
                5!
                """, new NumericValue.IntValue(120)),
            Arguments.of("""
                42%
                """, new NumericValue.RealValue(0.42))
        );
    }

    @ParameterizedTest
    @MethodSource("postfixExpression")
    void postfixEvaluation_scopeNotModified_numericValueReturned(String src, Value expected) {
        Scope scopeInitial = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scopeInitial, registry, List.of());

        AlgebraicParser.TermContext termContext = extractTerm(src);

        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> termEval = visitor.visitTerm(termContext);

        EvalResult<Value> result = termEval.run(executionContext);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(expected, actual.value()),
                () -> assertEquals(scopeInitial, actual.executionContext().scope())
            );

            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interrupt: " + interrupted.reason());
        }
    }

    private static Stream<Arguments> unaryExpression() {
        return Stream.of(
            Arguments.of("""
                1!
                """, new NumericValue.IntValue(1)),
            Arguments.of("""
                100%
                """, new NumericValue.RealValue(1))
        );
    }

    @ParameterizedTest
    @MethodSource("unaryExpression")
    void unaryEvaluation_scopeNotModified_numericValueReturned(String src, Value expected) {
        Scope scopeInitial = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scopeInitial, registry, List.of());

        AlgebraicParser.TermContext termContext = extractTerm(src);

        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> termEval = visitor.visitTerm(termContext);

        EvalResult<Value> result = termEval.run(executionContext);

        switch (result) {
            case EvalResult.Returned<Value> returned -> assertAll(
                () -> assertEquals(expected, returned.value()),
                () -> assertEquals(scopeInitial, returned.executionContext().scope())
            );

            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interrupt: " + interrupted.reason());
        }
    }

    private static Stream<Arguments> powerExpression() {
        return Stream.of(
            Arguments.of("""
                1^1
                """, new NumericValue.IntValue(1)),
            Arguments.of("""
                2^2
                """, new NumericValue.IntValue(4)),
            Arguments.of("""
                2.0^2
                """, new NumericValue.RealValue(4)),
            Arguments.of("""
                2^2.0
                """, new NumericValue.RealValue(4)),
            Arguments.of("""
                2.0^2.0
                """, new NumericValue.RealValue(4))
        );
    }

    @ParameterizedTest
    @MethodSource("powerExpression")
    void powerEvaluation_scopeNotModified_numericValueReturned(String src, Value expected) {
        Scope scopeInitial = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scopeInitial, registry, List.of());

        AlgebraicParser.TermContext termContext = extractTerm(src);

        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> termEval = visitor.visitTerm(termContext);

        EvalResult<Value> result = termEval.run(executionContext);

        switch (result) {
            case EvalResult.Returned<Value> actual -> assertAll(
                () -> assertEquals(expected, actual.value()),
                () -> assertEquals(scopeInitial, actual.executionContext().scope())
            );

            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interrupt: " + interrupted.reason());
        }
    }

    private static Stream<Arguments> numberCases() {
        return Stream.of(
            Arguments.of("5", new NumericValue.IntValue(5)),
            Arguments.of("5.0", new NumericValue.RealValue(5)),
            Arguments.of(".5", new NumericValue.RealValue(.5)),
            Arguments.of("5.", new NumericValue.RealValue(5))
        );
    }

    @ParameterizedTest
    @MethodSource("numberCases")
    void evaluateNumber(String src, NumericValue expected) {
        EvalResult<Value> result = interpreter.evaluate(src, context);
        switch (result) {
            case EvalResult.Returned<Value> actual -> assertEquals(expected, actual.value());
            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interruption: " + interrupted);
        }

    }


}
