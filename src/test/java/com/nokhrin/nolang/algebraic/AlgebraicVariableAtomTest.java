/**
 * com.nokhrin.nolang.AlgebraicParser
 * точка входа - ProgramContext
 * -> программа не пуста - приведение к ProgramWithStatementsContext (может содержать список, брать i=0)
 * -> возврат StatementContext
 * -> возврат AssignmentContext
 * -> присваивания нет - не ID = ... - пропуск AssignStatementContext
 *  -> выбор TermStatementContext, приведение
 *  -> TermContext (может содержать список, брать i=0)
 *  -> FactorContext (может содержать список, брать i=0)
 *  -> в лексеме нет унарного +/- - пропуск UnaryExpressionContext
 *  -> в лексеме нет ^ - пропуск PowerExpressionContext
 *   -> выбор, приведение PostfixExpressionContext (может содержать список, брать i=0)
 *   -> возврат PostfixContext
 *   -> возврат AtomContext
 *     -> в лексеме нет | | - не |ID| - пропуск AbsoluteAtomContext
 *     -> в лексеме нет () - не (ID) - пропуск ParenthesesAtomContext
 *     -> в лексеме нет NUM - пропуск NumberAtomContext
 *     -> в лексеме нет (args) - пропуск FuncCallAtomContext
 *      -> в лексеме только ID - VariableAtomContext
 *       -> приведение VariableAtomContext
 *
 * Итоговая цепочка:
 * ProgramContext
 * -> ProgramWithStatementsContext(0)
 *  -> StatementContext
 *   -> AssignmentContext
 *    -> TermContext(0)
 *     -> FactorContext(0)
 *      -> PostfixExpressionContext(0)
 *       -> VariableAtomContext
 */
package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlgebraicVariableAtomTest {

    /**
     * ProgramContext - cast to ProgramWithStatementsContext
     * -> ProgramWithStatementsContext(0) - call StatementContext
     *  -> StatementContext - call AssignmentContext
     *   -> AssignmentContext - cast to Term
     *    -> TermContext(0) - call
     *     -> FactorContext(0) - cast to PostfixExpression
     *      -> PostfixExpressionContext(0) - cast to VarAtom
     *       -> VariableAtomContext
     * @return
     */
    private AlgebraicParser.VariableAtomContext extractVarCtx(String src) {
        Either<List<EvalError.SyntaxError>, AlgebraicParser.ProgramContext> parsed = AlgebraicSyntaxAnalyzer.parse(src);
        if (parsed.isLeft()) {
            throw new AssertionError("Parsing failed: " + parsed.leftOptional().orElseThrow());
        }
        var program = (AlgebraicParser.ProgramWithStatementsContext) parsed.rightOptional().orElseThrow();
        var stmt = (AlgebraicParser.StatementContext) program.statement(0);
        var assign = (AlgebraicParser.AssignmentContext) stmt.assignment();
        var term = (AlgebraicParser.TermStatementContext) assign;
        var factor = term.term().factor(0);
        var postfix = (AlgebraicParser.PostfixExpressionContext) factor.unary(0);
        var variable = (AlgebraicParser.VariableAtomContext) postfix.postfix().atom();
        return variable;
    }

    @Test
    void readVariableDeclared_FoundInScope() {
        //определить переменную
        String varName = "x";
        NumericValue.IntValue varValue = new NumericValue.IntValue(42);

        //определить Scope с defined переменной
        Scope scope = new Scope()
            .define(varName, varValue)
            .rightOptional()
            .orElseThrow(() -> new AssertionError("Failed to init scope"));

        //определить контекст вычисления
        FunctionRegistry functionRegistry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scope, functionRegistry, List.of());

        //определить ast путь
        //извлечь AlgebraicParser.VariableAtomContext из ast
        AlgebraicParser.VariableAtomContext varAtomCtx = extractVarCtx(varName);

        //построить вычисление
        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> atomEval = visitor.visitVariableAtom(varAtomCtx);

        //выполнить вычисление в контексте
        EvalResult<Value> evalResult = atomEval.run(executionContext);

        //проверить значение|ошибку
        switch (evalResult) {
            case EvalResult.Returned<Value> returned -> {
                assertAll(
//                    ()-> assertEquals(varName, returned.value()),
                    () -> assertEquals(varValue, returned.value())
                );
            }
            case EvalResult.Interrupted<Value> interrupted ->
                fail("Unexpected interruption: " + interrupted.reason().message());
        }
    }

    @Test
    void readVariableNotDeclared_ErrorReturned() {
        //определить переменную
        String varName = "x";
        NumericValue.IntValue varValue = new NumericValue.IntValue(42);

        //определить Scope без defined переменной
        Scope scope = new Scope();

        //определить контекст вычисления
        FunctionRegistry functionRegistry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scope, functionRegistry, List.of());

        //определить ast путь
        //извлечь AlgebraicParser.VariableAtomContext из ast
        AlgebraicParser.VariableAtomContext varAtomCtx = extractVarCtx(varName);

        //построить вычисление
        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> atomEval = visitor.visitVariableAtom(varAtomCtx);

        //выполнить вычисление в контексте
        EvalResult<Value> evalResult = atomEval.run(executionContext);

        //проверить значение|ошибку
        switch (evalResult) {
            case EvalResult.Interrupted<Value> interrupted -> {
                assertInstanceOf(EvalResult.Interrupted.class, interrupted);
                assertInstanceOf(InterruptReason.Error.class, interrupted.reason());
                switch (interrupted.reason()) {
                    case InterruptReason.Error error ->
                        assertInstanceOf(ScopeError.UndefinedVariable.class, error.cause());
                    case InterruptReason.Control signal -> fail("unexpected control signal: " + signal);
                }
            }
            case EvalResult.Returned<Value> returned -> fail("Unexpected return: " + returned.value());
        }

    }

}
