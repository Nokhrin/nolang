package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicBaseVisitor;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.combinators.*;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.operations.BinaryNumericOperation;
import com.nokhrin.nolang.common.operations.UnaryNumericOperation;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

import java.util.List;

import static com.nokhrin.nolang.common.combinators.ValueParser.parseNumber;

public class AlgebraicEvalVisitor extends AlgebraicBaseVisitor<Eval<Value>> {
    /**
     * returns last statement
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Eval<Value> visitProgramWithStatements(AlgebraicParser.ProgramWithStatementsContext ctx) {
        Eval<Value> lastEval = Eval.pure(Value.VoidValue.INSTANCE);

        for (AlgebraicParser.StatementContext statementContext : ctx.statement()) {
            lastEval = lastEval.flatMap(_ -> visit(statementContext));
        }

        return lastEval;
    }

    /**
     * makes no computations
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Eval<Value> visitEmptyProgram(AlgebraicParser.EmptyProgramContext ctx) {
        return Eval.pure(Value.VoidValue.INSTANCE);
    }

    /**
     * delegates to assignment
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Eval<Value> visitStatement(AlgebraicParser.StatementContext ctx) {
        return visit(ctx.assignment());
    }

    /**
     * assigns, updates scope
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Eval<Value> visitAssignStatement(AlgebraicParser.AssignStatementContext ctx) {
        String varName = ctx.ID().getText();
        Eval<Value> varValueEval = visit(ctx.term());

        return ScopeCombinators.assignVariable(varName, varValueEval);
    }

    /**
     * makes computation, no assign
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Eval<Value> visitTermStatement(AlgebraicParser.TermStatementContext ctx) {
        Eval<Value> valueEval = visit(ctx.term());

        valueEval = valueEval.flatMap(value ->
            value.match(
                Eval::pure,
                boolValue -> Eval.raiseError(new EvalError.TypeError("Numeric expected, got: " + boolValue)),
                voidValue -> Eval.raiseError(new EvalError.TypeError("Numeric expected, got: " + voidValue))
            )
        );
        return valueEval;
    }

    @Override
    public Eval<Value> visitTerm(AlgebraicParser.TermContext ctx) {

        List<AlgebraicParser.FactorContext> factors = ctx.factor();
        List<AlgebraicParser.AddOpContext> addOps = ctx.addOp();

        Eval<NumericValue> numericValueEval =
            Folds.foldLeftAssociativeNumeric(factors, addOps,
                node -> visit(node)
                    .flatMap(ValueCombinators::narrowToNumericValue));

        return EvalCombinators.upcastToValue(numericValueEval);
    }

    @Override
    public Eval<Value> visitFactor(AlgebraicParser.FactorContext ctx) {
        List<AlgebraicParser.UnaryContext> unaries = ctx.unary();
        List<AlgebraicParser.MulOpContext> mulOps = ctx.mulOp();

        Eval<NumericValue> numericValueEval =
            Folds.foldLeftAssociativeNumeric(unaries, mulOps,
                node -> visit(node)
                    .flatMap(ValueCombinators::narrowToNumericValue));

        return EvalCombinators.upcastToValue(numericValueEval);
    }

    @Override
    public Eval<Value> visitUnaryExpression(AlgebraicParser.UnaryExpressionContext ctx) {
        String unOp = ctx.unaryOp().getText();
        Eval<Value> operand = visit(ctx.unary());

        Eval<NumericValue> operandNumeric = operand.flatMap(ValueCombinators::narrowToNumericValue);
        Eval<NumericValue> result = UnaryNumericOperation.applySymbol(unOp, operandNumeric);

        return EvalCombinators.upcastToValue(result);
    }

    @Override
    public Eval<Value> visitPowerExpression(AlgebraicParser.PowerExpressionContext ctx) {
        Eval<Value> baseEval = visit(ctx.postfix());
        Eval<Value> exponentEval = visit(ctx.unary());

        Eval<NumericValue> baseNumeric = baseEval.flatMap(ValueCombinators::narrowToNumericValue);
        Eval<NumericValue> exponentNumeric = exponentEval.flatMap(ValueCombinators::narrowToNumericValue);

        Eval<NumericValue> result = BinaryNumericOperation.POW.apply(baseNumeric, exponentNumeric);

        return EvalCombinators.upcastToValue(result);
    }

    @Override
    public Eval<Value> visitPostfixExpression(AlgebraicParser.PostfixExpressionContext ctx) {
        Eval<Value> atomEval = visit(ctx.postfix().atom());
        Eval<NumericValue> resultAccum = atomEval.flatMap(ValueCombinators::narrowToNumericValue);

        for (AlgebraicParser.PostfixOpContext postOp : ctx.postfix().postfixOp()) {
            String operation = postOp.getText();
            resultAccum = UnaryNumericOperation.applySymbol(operation, resultAccum);
        }

        return EvalCombinators.upcastToValue(resultAccum);
    }

    @Override
    public Eval<Value> visitPostfix(AlgebraicParser.PostfixContext ctx) {
        return super.visitPostfix(ctx);
    }

    @Override
    public Eval<Value> visitAbsoluteAtom(AlgebraicParser.AbsoluteAtomContext ctx) {
        return super.visitAbsoluteAtom(ctx);
    }

    @Override
    public Eval<Value> visitParenthesesAtom(AlgebraicParser.ParenthesesAtomContext ctx) {
        return super.visitParenthesesAtom(ctx);
    }

    @Override
    public Eval<Value> visitNumberAtom(AlgebraicParser.NumberAtomContext ctx) {
        String numberStr = ctx.NUM().getText();
        Eval<NumericValue> number = parseNumber(numberStr);
        return EvalCombinators.upcastToValue(number);
    }

    @Override
    public Eval<Value> visitFuncCallAtom(AlgebraicParser.FuncCallAtomContext ctx) {
        return super.visitFuncCallAtom(ctx);
    }

    @Override
    public Eval<Value> visitVariableAtom(AlgebraicParser.VariableAtomContext ctx) {
        String varName = ctx.ID().getText();
        Eval<ExecutionContext> executionContextEval = ContextCombinators.getContext();
        Eval<Value> varInContext = executionContextEval
            .flatMap(executionContext ->
                executionContext.scope().lookup(varName)
                    .fold(Eval::raiseError, Eval::pure));
        return varInContext;
    }

    @Override
    public Eval<Value> visitArguments(AlgebraicParser.ArgumentsContext ctx) {
        return super.visitArguments(ctx);
    }

    @Override
    public Eval<Value> visitMulOp(AlgebraicParser.MulOpContext ctx) {
        return super.visitMulOp(ctx);
    }

    @Override
    public Eval<Value> visitAddOp(AlgebraicParser.AddOpContext ctx) {
        return super.visitAddOp(ctx);
    }

    @Override
    public Eval<Value> visitUnaryOp(AlgebraicParser.UnaryOpContext ctx) {
        return super.visitUnaryOp(ctx);
    }

    @Override
    public Eval<Value> visitPostfixOp(AlgebraicParser.PostfixOpContext ctx) {
        return super.visitPostfixOp(ctx);
    }
}
