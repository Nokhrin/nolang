package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicBaseVisitor;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.operations.BinaryNumericOperation;
import com.nokhrin.nolang.common.operations.UnaryNumericOperation;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.values.VoidValue;
import com.nokhrin.nolang.functional.*;

import java.util.List;

import static com.nokhrin.nolang.common.values.ValueParser.parseNumber;

public class AlgebraicEvalVisitor extends AlgebraicBaseVisitor<Eval<Value>> {

    @Override
    public Eval<Value> visitProgramWithStatements(AlgebraicParser.ProgramWithStatementsContext ctx) {
        Eval<Value> accumulator = Eval.pure(VoidValue.INSTANCE);
        for (AlgebraicParser.StatementContext statement : ctx.statement()) {
            accumulator = accumulator.flatMap(_ -> visit(statement));
        }
        return accumulator;
    }

    @Override
    public Eval<Value> visitEmptyProgram(AlgebraicParser.EmptyProgramContext ctx) {
        return Eval.pure(VoidValue.INSTANCE);
    }

    @Override
    public Eval<Value> visitStatement(AlgebraicParser.StatementContext ctx) {
        return visit(ctx.assignment());
    }

    @Override
    public Eval<Value> visitAssignStatement(AlgebraicParser.AssignStatementContext ctx) {
        String varName = ctx.ID().getText();
        return visit(ctx.term())
            .flatMap(varValue -> EnvironmentCombinators.modifyScope(scope -> scope.assignOrDefine(varName, varValue)));
    }

    @Override
    public Eval<Value> visitTermStatement(AlgebraicParser.TermStatementContext ctx) {
        return visit(ctx.term());
    }

    @Override
    public Eval<Value> visitTerm(AlgebraicParser.TermContext ctx) {
        return Folds.foldLeftAssociative(ctx.factor(), ctx.addOp(), this::visit);
    }

    @Override
    public Eval<Value> visitFactor(AlgebraicParser.FactorContext ctx) {
        return Folds.foldLeftAssociative(ctx.unary(), ctx.mulOp(), this::visit);
    }

    @Override
    public Eval<Value> visitUnaryExpression(AlgebraicParser.UnaryExpressionContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        UnaryNumericOperation operation =
            UnaryNumericOperation.fromSymbol(ctx.unaryOp().getText());
        return visit(ctx.unary())
            .flatMap(value ->
                OperationEvaluator.apply(operation, value));
    }

    @Override
    public Eval<Value> visitPowerExpression(AlgebraicParser.PowerExpressionContext ctx) {
        return visit(ctx.postfix())
            .flatMap(
                base ->
                    visit(ctx.unary())
                        .flatMap(
                            exponent ->
                                OperationEvaluator.apply(BinaryNumericOperation.POW, base, exponent)));
    }

    @Override
    public Eval<Value> visitPostfixExpression(AlgebraicParser.PostfixExpressionContext ctx) {
        return visit(ctx.postfix());
    }

    @Override
    public Eval<Value> visitPostfix(AlgebraicParser.PostfixContext ctx) {
        Eval<Value> accumulator = visit(ctx.atom());
        for (AlgebraicParser.PostfixOpContext postfixOp : ctx.postfixOp()) {
            UnaryNumericOperation operation = UnaryNumericOperation.fromSymbol(postfixOp.getText());
            accumulator = accumulator
                .flatMap(value -> OperationEvaluator.apply(operation, value));
        }
        return accumulator;
    }

    @Override
    public Eval<Value> visitAbsoluteAtom(AlgebraicParser.AbsoluteAtomContext ctx) {
        return visit(ctx.term())
            .flatMap(value -> OperationEvaluator.apply(UnaryNumericOperation.ABSOLUTE, value));
    }

    @Override
    public Eval<Value> visitParenthesesAtom(AlgebraicParser.ParenthesesAtomContext ctx) {
        return visit(ctx.term());
    }

    @Override
    public Eval<Value> visitNumberAtom(AlgebraicParser.NumberAtomContext ctx) {
        String lexeme = ctx.NUM().getText();
        return parseNumber(lexeme).map(v -> v);
    }

    @Override
    public Eval<Value> visitFuncCallAtom(AlgebraicParser.FuncCallAtomContext ctx) {
        String funcName = ctx.ID().getText();
        List<Eval<Value>> argsEval = ctx.arguments().term().stream()
            .map(this::visit)
            .toList();
        return Folds.collectArguments(argsEval)
            .flatMap(args -> EnvironmentCombinators.getEnvironment()
                .flatMap(env -> env.registry().invoke(funcName, args)));
    }

    @Override
    public Eval<Value> visitVariableAtom(AlgebraicParser.VariableAtomContext ctx) {
        String varName = ctx.ID().getText();

        return EnvironmentCombinators.getEnvironment()
            .flatMap(
                env ->
                    env.scope()
                        .lookup(varName)
                        .map(Eval::pure)
                        .orElseGet(() -> Eval.raiseError(new ScopeError.Undefined("Undefined variable: " + varName))
                        )
            );
    }
}
