package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicBaseVisitor;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.definitions.VariableSymbol;
import com.nokhrin.nolang.common.executions.Result;
import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.executions.ValueResult;
import com.nokhrin.nolang.common.executions.VoidResult;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

public class AlgebraicEvalVisitor extends AlgebraicBaseVisitor<Result> {
  private final Scope scope;

  public AlgebraicEvalVisitor(Scope scope) {
    this.scope = scope;
  }

  @Override
  public Result visitProgram(AlgebraicParser.ProgramContext ctx) {
    return program(ctx);
  }

  private Result program(AlgebraicParser.ProgramContext ctx) {
    Result result = new VoidResult();
    for (AlgebraicParser.StatementContext statementContext : ctx.statement()) {
      result = statement(statementContext);
    }
    return result;
  }

  private Result statement(AlgebraicParser.StatementContext ctx) {
    if (ctx.ID() != null) {
      Value value = expression(ctx.expression());
      VariableSymbol symbol = new VariableSymbol(ctx.ID().getText(), value.type());
      scope.define(symbol, value);
      return new VoidResult();
    }
    return new ValueResult(expression(ctx.expression()));
  }

  private Value expression(AlgebraicParser.ExpressionContext ctx) {
    return term(ctx.term());
  }

  private Value term(AlgebraicParser.TermContext term) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Result visitStatement(AlgebraicParser.StatementContext ctx) {
    return statement(ctx);
  }

  @Override
  public Result visitExpression(AlgebraicParser.ExpressionContext ctx) {
    return new ValueResult(expression(ctx));
  }

  @Override
  public Result visitTerm(AlgebraicParser.TermContext ctx) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Result visitFactor(AlgebraicParser.FactorContext ctx) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Result visitUnary(AlgebraicParser.UnaryContext ctx) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Result visitPower(AlgebraicParser.PowerContext ctx) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Result visitFactorial(AlgebraicParser.FactorialContext ctx) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Result visitAbsolute(AlgebraicParser.AbsoluteContext ctx) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Result visitNumber(AlgebraicParser.NumberContext ctx) {
    return new ValueResult(NumericValue.parse(ctx.NUM().getText()));
  }

  @Override
  public Result visitVariable(AlgebraicParser.VariableContext ctx) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Result visitParentheses(AlgebraicParser.ParenthesesContext ctx) {
    return visit(ctx.expression());
  }
}
