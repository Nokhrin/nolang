package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicBaseVisitor;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.executions.Result;
import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.executions.ValueResult;
import com.nokhrin.nolang.common.executions.VoidResult;
import com.nokhrin.nolang.common.operations.*;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

import static com.nokhrin.nolang.common.values.ValueParser.parseNumber;

public class AlgebraicEvalVisitor extends AlgebraicBaseVisitor<Result> {
    private final Scope scope;

    public AlgebraicEvalVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Result visitProgram(AlgebraicParser.ProgramContext ctx) {
        Result lastResult = new VoidResult();
        for (AlgebraicParser.StatementContext statement : ctx.statement()) {
            lastResult = visit(statement);
        }
        return lastResult;
    }

    @Override
    public Result visitStatement(AlgebraicParser.StatementContext ctx) {
        return visit(ctx.assignment());
    }

    @Override
    public Result visitAssignStatement(AlgebraicParser.AssignStatementContext ctx) {
        String varName = ctx.ID().getText();
        Value varValue = visit(ctx.term()).asValue();

        if (scope.contains(varName)) {
            scope.assign(varName, varValue);
        } else {
            scope.declare(varName, varValue);
        }

        return new VoidResult();
    }

    @Override
    public Result visitTermStatement(AlgebraicParser.TermStatementContext ctx) {
        return visit(ctx.term());
    }

    @Override
    public Result visitTerm(AlgebraicParser.TermContext ctx) {

        List<NumericValue> values = ctx.factor().stream()
            .map(this::visit)
            .map(Result::asNumericValue)
            .toList();

        List<BinaryOperation> operations = ctx.children.stream()
            .filter(TerminalNode.class::isInstance)
            .map(ParseTree::getText)
            .map(BinaryOperation::fromSymbol)
            .toList();

        return new ValueResult(Folds.left(values, operations));
    }

    @Override
    public Result visitFactor(AlgebraicParser.FactorContext ctx) {

        List<NumericValue> values = ctx.unary().stream()
            .map(this::visit)
            .map(Result::asNumericValue)
            .toList();

        List<BinaryOperation> operations = ctx.children.stream()
            .filter(TerminalNode.class::isInstance)
            .map(ParseTree::getText)
            .map(BinaryOperation::fromSymbol)
            .toList();

        return new ValueResult(Folds.left(values, operations));
    }

    @Override
    public Result visitUnaryExpression(AlgebraicParser.UnaryExpressionContext ctx) {
        PrefixOperation operator = PrefixOperation.fromSymbol(ctx.getChild(0).getText());
        NumericValue number = visit(ctx.unary()).asNumericValue();
        return new ValueResult(operator.apply(number));
    }

    @Override
    public Result visitPowerExpression(AlgebraicParser.PowerExpressionContext ctx) {
        NumericValue base = visit(ctx.factorial()).asNumericValue();
        NumericValue exponent = visit(ctx.unary()).asNumericValue();
        return new ValueResult(BinaryOperation.POW.apply(base, exponent));
    }

    @Override
    public Result visitAbsoluteAtom(AlgebraicParser.AbsoluteAtomContext ctx) {
        NumericValue value = visit(ctx.term()).asNumericValue();
        return new ValueResult(InfixOperation.ABSOLUTE.apply(value));
    }

    @Override
    public Result visitFactorial(AlgebraicParser.FactorialContext ctx) {
        NumericValue value = visit(ctx.atom()).asNumericValue();
        if (ctx.getChildCount() == 2) {
            return new ValueResult(PostfixOperation.FACTORIAL.apply(value));
        }
        return new ValueResult(value);
    }

    @Override
    public Result visitParenthesesAtom(AlgebraicParser.ParenthesesAtomContext ctx) {
        return visit(ctx.term());
    }

    @Override
    public Result visitNumberAtom(AlgebraicParser.NumberAtomContext ctx) {
        String lexeme = ctx.NUM().getText();
        return new ValueResult(parseNumber(lexeme));
    }

    @Override
    public Result visitVariableAtom(AlgebraicParser.VariableAtomContext ctx) {
        return new ValueResult(scope.fetch(ctx.ID().getText()));
    }
}
