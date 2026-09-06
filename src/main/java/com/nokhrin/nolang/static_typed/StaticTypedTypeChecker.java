package com.nokhrin.nolang.static_typed;

import com.nokhrin.nolang.StaticTypedBaseVisitor;
import com.nokhrin.nolang.StaticTypedParser;
import com.nokhrin.nolang.common.Type;

public class StaticTypedTypeChecker extends StaticTypedBaseVisitor<Type> {

    @Override
    public Type visitProg(StaticTypedParser.ProgContext ctx) {
        return super.visitProg(ctx);
    }

    @Override
    public Type visitDecl(StaticTypedParser.DeclContext ctx) {
        return super.visitDecl(ctx);
    }

    @Override
    public Type visitStat(StaticTypedParser.StatContext ctx) {
        return super.visitStat(ctx);
    }

    @Override
    public Type visitFuncDecl(StaticTypedParser.FuncDeclContext ctx) {
        return super.visitFuncDecl(ctx);
    }

    @Override
    public Type visitFuncSignature(StaticTypedParser.FuncSignatureContext ctx) {
        return super.visitFuncSignature(ctx);
    }

    @Override
    public Type visitFuncParameters(StaticTypedParser.FuncParametersContext ctx) {
        return super.visitFuncParameters(ctx);
    }

    @Override
    public Type visitFuncParameter(StaticTypedParser.FuncParameterContext ctx) {
        return super.visitFuncParameter(ctx);
    }

    @Override
    public Type visitVarDecl(StaticTypedParser.VarDeclContext ctx) {
        return super.visitVarDecl(ctx);
    }

    @Override
    public Type visitBlock(StaticTypedParser.BlockContext ctx) {
        return super.visitBlock(ctx);
    }

    @Override
    public Type visitType(StaticTypedParser.TypeContext ctx) {
        return super.visitType(ctx);
    }

    @Override
    public Type visitReturnStat(StaticTypedParser.ReturnStatContext ctx) {
        return super.visitReturnStat(ctx);
    }

    @Override
    public Type visitIfStat(StaticTypedParser.IfStatContext ctx) {
        return super.visitIfStat(ctx);
    }

    @Override
    public Type visitWhileStat(StaticTypedParser.WhileStatContext ctx) {
        return super.visitWhileStat(ctx);
    }

    @Override
    public Type visitBreakStat(StaticTypedParser.BreakStatContext ctx) {
        return super.visitBreakStat(ctx);
    }

    @Override
    public Type visitContinueStat(StaticTypedParser.ContinueStatContext ctx) {
        return super.visitContinueStat(ctx);
    }

    @Override
    public Type visitCall(StaticTypedParser.CallContext ctx) {
        return super.visitCall(ctx);
    }

    @Override
    public Type visitArguments(StaticTypedParser.ArgumentsContext ctx) {
        return super.visitArguments(ctx);
    }

    @Override
    public Type visitExpr(StaticTypedParser.ExprContext ctx) {
        return super.visitExpr(ctx);
    }

    @Override
    public Type visitAssign(StaticTypedParser.AssignContext ctx) {
        return super.visitAssign(ctx);
    }

    @Override
    public Type visitTernary(StaticTypedParser.TernaryContext ctx) {
        return super.visitTernary(ctx);
    }

    @Override
    public Type visitOr(StaticTypedParser.OrContext ctx) {
        return super.visitOr(ctx);
    }

    @Override
    public Type visitAnd(StaticTypedParser.AndContext ctx) {
        return super.visitAnd(ctx);
    }

    @Override
    public Type visitComparison(StaticTypedParser.ComparisonContext ctx) {
        return super.visitComparison(ctx);
    }

    @Override
    public Type visitAddSub(StaticTypedParser.AddSubContext ctx) {
        return super.visitAddSub(ctx);
    }

    @Override
    public Type visitMulDiv(StaticTypedParser.MulDivContext ctx) {
        return super.visitMulDiv(ctx);
    }

    @Override
    public Type visitNot(StaticTypedParser.NotContext ctx) {
        return super.visitNot(ctx);
    }

    @Override
    public Type visitNeg(StaticTypedParser.NegContext ctx) {
        return super.visitNeg(ctx);
    }

    @Override
    public Type visitPos(StaticTypedParser.PosContext ctx) {
        return super.visitPos(ctx);
    }

    @Override
    public Type visitPrime(StaticTypedParser.PrimeContext ctx) {
        return super.visitPrime(ctx);
    }

    @Override
    public Type visitFloat(StaticTypedParser.FloatContext ctx) {
        return super.visitFloat(ctx);
    }

    @Override
    public Type visitInt(StaticTypedParser.IntContext ctx) {
        return super.visitInt(ctx);
    }

    @Override
    public Type visitBool(StaticTypedParser.BoolContext ctx) {
        return super.visitBool(ctx);
    }

    @Override
    public Type visitFuncCall(StaticTypedParser.FuncCallContext ctx) {
        return super.visitFuncCall(ctx);
    }

    @Override
    public Type visitId(StaticTypedParser.IdContext ctx) {
        return super.visitId(ctx);
    }

    @Override
    public Type visitParen(StaticTypedParser.ParenContext ctx) {
        return super.visitParen(ctx);
    }
}
