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

public class VisitorInductionBasisTest extends AlgebraicIntegrationTestBase {


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
        var program = parsed.rightOptional().orElseThrow();

        if (program instanceof AlgebraicParser.ProgramWithStatementsContext pwsc) {
            var stmt = pwsc.statement(0);
            var assign = stmt.assignment();
            if (assign instanceof AlgebraicParser.TermStatementContext tsc) {
                var term = tsc.term();
                var factor = term.factor(0);
                var postfix = factor.unary(0);
                if (postfix instanceof AlgebraicParser.PostfixExpressionContext pec) {
                    var atom = pec.postfix().atom();
                    if (atom instanceof AlgebraicParser.VariableAtomContext vac)
                        return vac;

                }
            }
        }
        throw new AssertionError("VariableAtomContext not found in AST for source: " + src);
    }

    @Test
    void readVariableDeclared_FoundInScope() {
        String varName = "x";
        NumericValue.IntValue varValue = new NumericValue.IntValue(42);

        Scope scope = new Scope()
            .define(varName, varValue)
            .rightOptional()
            .orElseThrow(() -> new AssertionError("Failed to init scope"));

        FunctionRegistry functionRegistry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scope, functionRegistry, List.of());

        AlgebraicParser.VariableAtomContext varAtomCtx = extractVarCtx(varName);

        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> atomEval = visitor.visitVariableAtom(varAtomCtx);

        EvalResult<Value> evalResult = atomEval.run(executionContext);

        switch (evalResult) {
            case EvalResult.Returned<Value> returned -> {
                assertAll(
                    () -> assertEquals(varValue, returned.value())
                );
            }
            case EvalResult.Interrupted<Value> interrupted ->
                fail("Unexpected interruption: " + interrupted.reason().message());
        }
    }

    @Test
    void readVariableNotDeclared_ErrorReturned() {
        String varName = "x";

        Scope scope = new Scope();

        FunctionRegistry functionRegistry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext executionContext = new ExecutionContext(scope, functionRegistry, List.of());

        AlgebraicParser.VariableAtomContext varAtomCtx = extractVarCtx(varName);

        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> atomEval = visitor.visitVariableAtom(varAtomCtx);

        EvalResult<Value> evalResult = atomEval.run(executionContext);

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

    static Stream<Arguments> validNumberAtoms() {
        return Stream.of(
            Arguments.of("0", new NumericValue.IntValue(0)),
            Arguments.of("1", new NumericValue.IntValue(1)),
            Arguments.of("123", new NumericValue.IntValue(123)),
            Arguments.of(".5", new NumericValue.RealValue(0.5)),
            Arguments.of("5.", new NumericValue.RealValue(5.0)),
            Arguments.of("1.25", new NumericValue.RealValue(1.25))
        );
    }

    @ParameterizedTest
    @MethodSource("validNumberAtoms")
    void evaluateArithmetic_actualEqualsExpected(String input, NumericValue expected) {
        EvalResult<Value> actual = interpreter.evaluate(input, context);
        switch (actual) {
            case EvalResult.Returned<Value> returned -> assertEquals(expected, returned.value());
            case EvalResult.Interrupted<Value> interrupted ->
                fail("Interrupted, reason: " + interrupted.reason().message());
        }
    }


    private AlgebraicParser.NumberAtomContext extractNumberCtx(String src) {
        Either<List<EvalError.SyntaxError>, AlgebraicParser.ProgramContext> parsedNumber = AlgebraicSyntaxAnalyzer.parse(src);

        if (parsedNumber.isLeft()) {

            fail("Error parsing " + src + " as number atom");
        }

        AlgebraicParser.ProgramContext programContext = parsedNumber.rightOptional().orElseThrow();

        if (programContext instanceof AlgebraicParser.ProgramWithStatementsContext pwsc) {
            var stmt = pwsc.statement(0);
            var assign = stmt.assignment();
            if (assign instanceof AlgebraicParser.TermStatementContext tsc) {
                var term = tsc.term();
                var factor = term.factor(0);
                var unary = factor.unary(0);
                if (unary instanceof AlgebraicParser.PostfixExpressionContext pec) {
                    var postfix = pec.postfix();
                    var atom = postfix.atom();
                    if (atom instanceof AlgebraicParser.NumberAtomContext nac) {
                        return nac;
                    }
                }
            }

        }
        throw new AssertionError("NumberContext not found");


    }

    @Test
    void zeroParsedAsNumberAtom() {
        // Получить NumberAtomContext
        AlgebraicParser.NumberAtomContext numberAtomContext = extractNumberCtx("0");

        //Создать пустой ExecutionContext
        Scope scope = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext context = new ExecutionContext(scope, registry, List.of());

        //Вызвать visitNumberAtom
        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> number = visitor.visitNumberAtom(numberAtomContext);

        //Выполнить вычисление
        EvalResult<Value> result = number.run(context);

        //Проверить результат
        switch (result) {
            case EvalResult.Returned<Value> returned -> {
                assertEquals(new NumericValue.IntValue(0), returned.value());
                assertEquals("0", numberAtomContext.NUM().getText());
            }
            case EvalResult.Interrupted<Value> interrupted ->
                fail("Unexpected interruption: " + interrupted.reason().message());
        }

    }
}
