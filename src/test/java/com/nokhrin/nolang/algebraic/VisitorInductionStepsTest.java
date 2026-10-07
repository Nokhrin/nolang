/*
Сценарии для visitTerm
Сценарий

Вход

Ожидаемый результат
Один фактор (число)

42

Returned(IntValue(42))
Два фактора, оператор +

1+2

Returned(IntValue(3))
Несколько факторов, операторы +

1+2+3

Returned(IntValue(6))
Смешанные операторы + и -

1+2-3

Returned(IntValue(0))
Ошибка деления на ноль

1/0

Interrupted(ArithmeticError)
Неинициализированная переменная

x (без x=...)

Interrupted(UndefinedVariable)
Сценарии для visitFactor
Сценарий

Вход

Ожидаемый результат
Один унарный (число)

42

Returned(IntValue(42))
Два унарных, оператор *

2*3

Returned(IntValue(6))
Несколько унарных, операторы *

2*3*4

Returned(IntValue(24))
Смешанные операторы * и /

6/2*3

Returned(IntValue(9))
Ошибка деления на ноль

1/0

Interrupted(ArithmeticError)
Неинициализированная переменная

x (без x=...)

Interrupted(UndefinedVariable)
 */
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

    @Test
    void numLiteral_scopeNotModified_numericValueReturned() {
        String src = """
            42
            """;
        NumericValue.IntValue numExpected = new NumericValue.IntValue(42);
        Scope scopeInitial = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scopeInitial, registry, List.of());

        AlgebraicParser.TermContext termContext = extractTerm(src);

        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> termEval = visitor.visitTerm(termContext);

        EvalResult<Value> result = termEval.run(executionContext);

        switch (result) {
            case EvalResult.Returned<Value> returned -> assertAll(
                () -> assertEquals(numExpected, returned.value()),
                () -> assertEquals(scopeInitial, returned.executionContext().scope())
            );

            case EvalResult.Interrupted<Value> interrupted -> fail("Unexpected interrupt: " + interrupted.reason());
        }
    }

    private static Stream<Arguments> arithmeticExpression() {
        return Stream.of(
            Arguments.of("""
                1+2*3
                """, 7L),
            Arguments.of("""
                1*2*3
                """, 6L),
            Arguments.of("""
                1*2+3
                """, 5L),
            Arguments.of("""
                9-2-3
                """, 4L)
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
    void expressionEvaluation_scopeNotModified_numericValueReturned(String src, Long expectedValue) {
        NumericValue.IntValue numExpected = new NumericValue.IntValue(expectedValue);
        Scope scopeInitial = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scopeInitial, registry, List.of());

        AlgebraicParser.TermContext termContext = extractTerm(src);

        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> termEval = visitor.visitTerm(termContext);

        EvalResult<Value> result = termEval.run(executionContext);

        switch (result) {
            case EvalResult.Returned<Value> returned -> assertAll(
                () -> assertEquals(numExpected, returned.value()),
                () -> assertEquals(scopeInitial, returned.executionContext().scope())
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
        var stmnt = (AlgebraicParser.StatementContext) program.statement(0);
        var assignParent = (AlgebraicParser.AssignmentContext) stmnt.assignment();

        var term = switch (assignParent) {
            case AlgebraicParser.AssignStatementContext assignStmnt -> assignStmnt.term();
            case AlgebraicParser.TermStatementContext termStmnt -> termStmnt.term();
            default -> throw new IllegalStateException("Unexpected value: " + assignParent);
        };
        return term;
    }

    @Test
    void functionCall_interrupted_typeErrorReturned() {
        String src = """
            print(1)
            """;
    }

    @Test
    void boolVal_interrupted_typeErrorReturned() {
        Value.BoolValue expectedValue = new Value.BoolValue(true);
    }

    @Test
    void calculationError_interrupted_ArithmeticErrorReturned() {

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
    }

    @Test
    void malformedDefinition_scopeWithoutVar_errorReturned() {
        String src = """
            x=1/0
            """;
    }

    @Test
    void defineWithVarNotInScope_scopeWithoutVar_errorReturned() {
        String src = """
            x=undefined_var
            """;
    }


    @Test
    void readUndefined_undefinedErrorReturned() {
        String src = """
            undefinedVar
            """;
        NumericValue.IntValue expected = new NumericValue.IntValue(2);
        EvalResult<Value> result = interpreter.evaluate(src, context);
        switch (result) {
            case EvalResult.Interrupted<Value> actual -> assertAll(
                () -> assertInstanceOf(InterruptReason.Error.class, actual.reason()),
                () -> assertFalse(actual.executionContext().scope().isDefined("undefinedVar"))
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


    }

    @Test
    void assign_voidResultReturned_scopeWithVarsCreated() {
        String src = """
            x=1+2
            x=8
            """;

    }

    @Test
    void assignAndCalculate_voidReturned_scopeWithVarsCreated() {
        String src = """
            x=1+2
            x+8
            """;

    }

    @Test
    void calculationInterruption_errorReturned_scopeWithFirstVarCreated() {
        String src = """
            x=1
            x/0
            x=2
            """;
    }

    @Test
    void defitionInterruption_errorReturned_scopeIsEmpty() {
        String src = """
            undefined_var
            x=1
            """;
    }

    @Test
    void visitStatement_delegates_scopeAndResultsNotModified() {
        Scope scopeInitial = new Scope();


        Scope scopeAfterVisit = new Scope();


        assertAll(
            () -> assertEquals(scopeInitial, scopeAfterVisit)
        );

    }


    @Test
    void parenthesesExpression() {
        String src = """
            (1)
            """;
    }

    @Test
    void absoluteExpression() {
        String src = """
            |1|
            """;

    }

    @Test
    void funcCallExpression() {
        String src = """
            func(x)
            """;

    }

    @Test
    void postfixExpression() {
        String src = """
            2!
            """;

//        String src = """
//            2%
//            """;
//
    }


    @Test
    void visitTerm_returnsVisitFactor() {
    }

    @Test
    void visitFactor_returnsVisitUnary() {
    }

    @Test
    void visitUnary_returnsVisitPostfix() {
    }

    @Test
    void visitPopstfix_returnsVisitAtom() {
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
