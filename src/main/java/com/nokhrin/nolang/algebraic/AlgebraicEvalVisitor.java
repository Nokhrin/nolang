package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicBaseVisitor;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.definitions.Symbol;
import com.nokhrin.nolang.common.definitions.VariableSymbol;
import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.values.DoubleValue;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.Value;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Optional;

import static com.nokhrin.nolang.common.operations.Arithmetic.*;

public class AlgebraicEvalVisitor extends AlgebraicBaseVisitor<Value> {
    private final Scope scope;

    public AlgebraicEvalVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Value visitProg(AlgebraicParser.ProgContext ctx) {
        Value lastResult = null;
        for (AlgebraicParser.StatContext statContext : ctx.stat()) {
            Value result = visitStat(statContext);
            if (result!=null){
                lastResult=result;
            }
        }
        return lastResult;
    }

    @Override
    public Value visitStat(AlgebraicParser.StatContext ctx) {
        if (ctx.expr() != null) {
            return visit(ctx.expr());
        }
        return null;
    }

    @Override
    public Value visitAssign(AlgebraicParser.AssignContext ctx) {
        String varName = ctx.ID().getText();
        Value value = visit(ctx.expr());
        VariableSymbol symbol = new VariableSymbol(varName, Optional.empty());
        scope.define(symbol, value);
        return value;
    }

    @Override
    public Value visitSumExpr(AlgebraicParser.SumExprContext ctx) {
        return visitSum(ctx.sum());
    }

    @Override
    public Value visitSum(AlgebraicParser.SumContext ctx) {
        Value left = visitMul(ctx.mul(0));
        for (int i = 1; i < ctx.mul().size(); i++) {
            Value right = visitMul(ctx.mul(i));
            ParseTree op = ctx.getChild(2 * i - 1);
            if (op.getText().equals("+")) {
                left = add(left, right);
            } else if (op.getText().equals("-")) {
                left = sub(left, right);
            }
        }
        return left;
    }

    @Override
    public Value visitMul(AlgebraicParser.MulContext ctx) {
        Value left = visitUnary(ctx.unary(0));
        for (int i = 1; i < ctx.unary().size(); i++) {
            Value right = visitUnary(ctx.unary(i));
            ParseTree op = ctx.getChild(2 * i - 1);
            if (op.getText().equals("*")) {
                left = mul(left, right);
            } else if (op.getText().equals("/")) {
                left = div(left, right);
            }
        }
        return left;

    }

    @Override
    public Value visitUnary(AlgebraicParser.UnaryContext ctx) {
        if (ctx.unary() != null) {
            Value number = visitUnary(ctx.unary());
            ParseTree op = ctx.getChild(0);
            if (op.getText().equals("-")) {
                return neg(number);
            } else if (op.getText().equals("+")) {
                return number;
            }
        }
        return visitPow(ctx.pow());
    }

    @Override
    public Value visitPow(AlgebraicParser.PowContext ctx) {
        Value base = visitFact(ctx.fact());
        if (ctx.pow() != null) {
            Value exponent = visitPow(ctx.pow());
            return pow(base, exponent);
        }
        return base;
    }

    @Override
    public Value visitFact(AlgebraicParser.FactContext ctx) {
        Value operand = visit(ctx.prim());
        if (ctx.getChildCount() > 1 && ctx.getChild(1).getText().equals("!")) {
            return factorial(operand);
        }
        return operand;
    }

    @Override
    public Value visitNum(AlgebraicParser.NumContext ctx) {
        String number = ctx.NUM().getText();
        if (number.contains(".")) {
            return new DoubleValue(Double.parseDouble(number));
        }
        return new IntValue(Long.parseLong(number));
    }

    @Override
    public Value visitId(AlgebraicParser.IdContext ctx) {
        String varName = ctx.ID().getText();
        Optional<Symbol> resolved = scope.resolve(varName);
        if (resolved.isEmpty()) {
            throw new IllegalStateException("Variable " + varName + " is used before assignment. Line: " + ctx.getStart().getLine());
        }
        return switch (resolved.get()) {
            case VariableSymbol variableSymbol -> scope.fetch(variableSymbol);
            default -> throw new IllegalStateException(varName + " cannot be used as variable name");
        };
    }

    @Override
    public Value visitMod(AlgebraicParser.ModContext ctx) {
        Value operand = visit(ctx.expr());
        return abs(operand);
    }

    @Override
    public Value visitGroup(AlgebraicParser.GroupContext ctx) {
        return visit(ctx.expr());
    }
}
