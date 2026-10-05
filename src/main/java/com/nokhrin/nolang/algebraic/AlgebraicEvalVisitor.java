package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicBaseVisitor;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.combinators.ContextCombinators;
import com.nokhrin.nolang.common.combinators.EvalCombinators;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

import static com.nokhrin.nolang.common.combinators.ValueParser.parseNumber;

public class AlgebraicEvalVisitor extends AlgebraicBaseVisitor<Eval<Value>> {
    @Override
    public Eval<Value> visitProgramWithStatements(AlgebraicParser.ProgramWithStatementsContext ctx) {
        /*
        пустая программа → Void
один оператор → результат оператора
несколько операторов → результат последнего
прерывание → прерывание всей программы
EOF игнорировать
         */
        return super.visitProgramWithStatements(ctx);
    }

    @Override
    public Eval<Value> visitEmptyProgram(AlgebraicParser.EmptyProgramContext ctx) {
        return super.visitEmptyProgram(ctx);
    }

    @Override
    public Eval<Value> visitStatement(AlgebraicParser.StatementContext ctx) {
        return super.visitStatement(ctx);
    }

    @Override
    public Eval<Value> visitAssignStatement(AlgebraicParser.AssignStatementContext ctx) {
        return super.visitAssignStatement(ctx);
    }

    @Override
    public Eval<Value> visitTermStatement(AlgebraicParser.TermStatementContext ctx) {
        return super.visitTermStatement(ctx);
    }

    @Override
    public Eval<Value> visitTerm(AlgebraicParser.TermContext ctx) {
        return super.visitTerm(ctx);
    }

    @Override
    public Eval<Value> visitFactor(AlgebraicParser.FactorContext ctx) {
        return super.visitFactor(ctx);
    }

    @Override
    public Eval<Value> visitUnaryExpression(AlgebraicParser.UnaryExpressionContext ctx) {
        return super.visitUnaryExpression(ctx);
    }

    @Override
    public Eval<Value> visitPowerExpression(AlgebraicParser.PowerExpressionContext ctx) {
        return super.visitPowerExpression(ctx);
    }

    @Override
    public Eval<Value> visitPostfixExpression(AlgebraicParser.PostfixExpressionContext ctx) {
        return super.visitPostfixExpression(ctx);
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
