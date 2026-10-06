package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TermVisitorTest {

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

    @Test
    void numExpression_scopeNotModified_numericValueReturned() {
        String src = """
            1+2*3
            """;

        NumericValue.IntValue numExpected = new NumericValue.IntValue(7);
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
}
