package com.nokhrin.nolang.dynamic_typed;

import static com.nokhrin.nolang.common.operations.Arithmetic.*;
import static com.nokhrin.nolang.common.operations.Logical.*;

import com.nokhrin.nolang.DynamicTypedBaseVisitor;
import com.nokhrin.nolang.DynamicTypedParser.*;
import com.nokhrin.nolang.DynamicTypedParser.BlockContext;
import com.nokhrin.nolang.DynamicTypedParser.StatContext;
import com.nokhrin.nolang.common.executions.*;
import com.nokhrin.nolang.common.values.*;

public class DynamicTypedEvalVisitor extends DynamicTypedBaseVisitor<Result> {
  private Scope currentScope;
  private final FunctionRegistry functionRegistry;

  public DynamicTypedEvalVisitor(Scope currentScope, FunctionRegistry functionRegistry) {
    this.currentScope = currentScope;
    this.functionRegistry = functionRegistry;
  }

  @Override
  public Result visitProg(ProgContext ctx) {
    return super.visitProg(ctx);
  }

  @Override
  public Result visitStat(StatContext ctx) {
    return super.visitStat(ctx);
  }

  @Override
  public Result visitFuncDef(FuncDefContext ctx) {
    return super.visitFuncDef(ctx);
  }

  @Override
  public Result visitFuncSignature(FuncSignatureContext ctx) {
    return super.visitFuncSignature(ctx);
  }

  @Override
  public Result visitFormalParameters(FormalParametersContext ctx) {
    return super.visitFormalParameters(ctx);
  }

  @Override
  public Result visitBlock(BlockContext ctx) {
    return super.visitBlock(ctx);
  }

  @Override
  public Result visitReturnStat(ReturnStatContext ctx) {
    return super.visitReturnStat(ctx);
  }

  @Override
  public Result visitAssignStat(AssignStatContext ctx) {
    return super.visitAssignStat(ctx);
  }

  @Override
  public Result visitType(TypeContext ctx) {
    return super.visitType(ctx);
  }

  @Override
  public Result visitIfStat(IfStatContext ctx) {
    return super.visitIfStat(ctx);
  }

  @Override
  public Result visitWhileStat(WhileStatContext ctx) {
    return super.visitWhileStat(ctx);
  }

  @Override
  public Result visitBreakStat(BreakStatContext ctx) {
    return super.visitBreakStat(ctx);
  }

  @Override
  public Result visitContinueStat(ContinueStatContext ctx) {
    return super.visitContinueStat(ctx);
  }

  @Override
  public Result visitCallExpr(CallExprContext ctx) {
    return super.visitCallExpr(ctx);
  }

  @Override
  public Result visitArguments(ArgumentsContext ctx) {
    return super.visitArguments(ctx);
  }

  @Override
  public Result visitExpr(ExprContext ctx) {
    return visit(ctx.ternary());
  }

  @Override
  public Result visitTernary(TernaryContext ctx) {
    return super.visitTernary(ctx);
  }

  @Override
  public Result visitOr(OrContext ctx) {
    return super.visitOr(ctx);
  }

  @Override
  public Result visitAnd(AndContext ctx) {
    return super.visitAnd(ctx);
  }

  @Override
  public Result visitComp(CompContext ctx) {
    return super.visitComp(ctx);
  }

  @Override
  public Result visitAddSub(AddSubContext ctx) {
    return super.visitAddSub(ctx);
  }

  @Override
  public Result visitMulDiv(MulDivContext ctx) {
    return super.visitMulDiv(ctx);
  }

  @Override
  public Result visitNot(NotContext ctx) {
    return super.visitNot(ctx);
  }

  @Override
  public Result visitNeg(NegContext ctx) {
    return super.visitNeg(ctx);
  }

  @Override
  public Result visitPos(PosContext ctx) {
    return super.visitPos(ctx);
  }

  @Override
  public Result visitPrime(PrimeContext ctx) {
    return super.visitPrime(ctx);
  }

  @Override
  public Result visitFloat(FloatContext ctx) {
    return super.visitFloat(ctx);
  }

  @Override
  public Result visitInt(IntContext ctx) {
    return super.visitInt(ctx);
  }

  @Override
  public Result visitBool(BoolContext ctx) {
    return super.visitBool(ctx);
  }

  @Override
  public Result visitVoid(VoidContext ctx) {
    return super.visitVoid(ctx);
  }

  @Override
  public Result visitFuncCall(FuncCallContext ctx) {
    return super.visitFuncCall(ctx);
  }

  @Override
  public Result visitId(IdContext ctx) {
    return super.visitId(ctx);
  }

  @Override
  public Result visitParen(ParenContext ctx) {
    return super.visitParen(ctx);
  }
}
