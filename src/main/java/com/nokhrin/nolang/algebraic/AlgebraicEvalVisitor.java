package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicBaseVisitor;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.combinators.*;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.operations.BinaryNumericOperation;
import com.nokhrin.nolang.common.operations.UnaryNumericOperation;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class AlgebraicEvalVisitor extends AlgebraicBaseVisitor<Eval<Value>> {

    @Override
    public Eval<Value> visitProgramWithStatements(AlgebraicParser.ProgramWithStatementsContext ctx) {
        Eval<Value> accumulator = Eval.pure(Value.VoidValue.INSTANCE);
        for (AlgebraicParser.StatementContext statement : ctx.statement()) {
            accumulator = accumulator.flatMap(_ -> visit(statement));
        }
        return accumulator;
    }

    @Override
    public Eval<Value> visitEmptyProgram(AlgebraicParser.EmptyProgramContext ctx) {
        return Eval.pure(Value.VoidValue.INSTANCE);
    }

    @Override
    public Eval<Value> visitStatement(AlgebraicParser.StatementContext ctx) {
        return visit(ctx.assignment());
    }

    @Override
    public Eval<Value> visitAssignStatement(AlgebraicParser.AssignStatementContext ctx) {
        String varName = ctx.ID().getText();
        return visit(ctx.term())
            .flatMap(
                varValue ->
                    ScopeCombinators.modifyScope(scope -> scope.assignOrDefine(varName, varValue)));
    }

    @Override
    public Eval<Value> visitTermStatement(AlgebraicParser.TermStatementContext ctx) {
        return evalTerm(ctx.term()).widen();
    }

    @Override
    public Eval<Value> visitFuncCallAtom(AlgebraicParser.FuncCallAtomContext ctx) {
        return evalFuncCall(ctx);
    }

    @Override
    public Eval<Value> visitVariableAtom(AlgebraicParser.VariableAtomContext ctx) {
        return evalVariable(ctx);
    }

    private Eval<Value> evalVariable(AlgebraicParser.VariableAtomContext ctx) {
        String varName = ctx.ID().getText();
        return EnvironmentCombinators.getEnvironment()
            .flatMap(env -> env.scope().lookup(varName).fold(Eval::raiseError, Eval::pure));
    }

    private Eval<NumericValue> evalTerm(AlgebraicParser.TermContext ctx) {
        return Folds.foldLeftAssociativeNumeric(ctx.factor(), ctx.addOp(), this::evalNumericNode);
    }

    private Eval<Value> evalFuncCall(AlgebraicParser.FuncCallAtomContext ctx) {
        String funcName = ctx.ID().getText();
        List<Eval<Value>> argsEval =
            ctx.arguments().term().stream().<Eval<Value>>map(term -> evalTerm(term).widen()).toList();
        return Folds.collectArguments(argsEval)
            .flatMap(args -> EnvironmentCombinators.callFunction(funcName, args));
    }

    private Eval<NumericValue> evalPostfix(AlgebraicParser.PostfixContext ctx) {
        Eval<NumericValue> accumulator = evalAtom(ctx.atom());
        for (AlgebraicParser.PostfixOpContext postfixOp : ctx.postfixOp()) {
            accumulator =
                accumulator.flatMap(
                    number ->
                        UnaryNumericOperation.fromSymbol(postfixOp.getText())
                            .fold(Eval::raiseError, operation -> operation.apply(number)));
        }
        return accumulator;
    }

    private Eval<NumericValue> evalNumericNode(ParseTree tree) {
        return switch (tree) {
            case AlgebraicParser.TermContext expr -> evalTerm(expr);
            case AlgebraicParser.FactorContext expr -> evalFactor(expr);
            case AlgebraicParser.UnaryContext expr -> evalUnaryOrPower(expr);
            case AlgebraicParser.PostfixContext expr -> evalPostfix(expr);
            case AlgebraicParser.AtomContext expr -> evalAtom(expr);
            default -> Eval.raiseError(new EvalError.SyntaxError("Unsupported numeric node: " + tree));
        };
    }

    private Eval<NumericValue> evalUnaryOrPower(AlgebraicParser.UnaryContext ctx) {
        return switch (ctx) {
            case AlgebraicParser.UnaryExpressionContext expr -> UnaryNumericOperation.applySymbol(
                expr.unaryOp().getText(), evalUnaryOrPower(expr.unary()));
            case AlgebraicParser.PowerExpressionContext expr -> evalPostfix(expr.postfix())
                .flatMap(
                    base ->
                        evalUnaryOrPower(expr.unary())
                            .flatMap(exponent -> BinaryNumericOperation.POW.apply(base, exponent)));
            case AlgebraicParser.PostfixExpressionContext expr -> evalPostfix(expr.postfix());
            default -> Eval.raiseError(new EvalError.SyntaxError("Unsupported unary node: " + ctx.getText()));
        };
    }

    private Eval<NumericValue> evalAtom(AlgebraicParser.AtomContext ctx) {
        return switch (ctx) {
            case AlgebraicParser.AbsoluteAtomContext expr ->
                evalTerm(expr.term()).flatMap(UnaryNumericOperation.ABSOLUTE::apply);
            case AlgebraicParser.ParenthesesAtomContext expr -> evalTerm(expr.term());
            case AlgebraicParser.NumberAtomContext expr -> ValueParser.parseNumber(expr.NUM().getText());
            case AlgebraicParser.FuncCallAtomContext expr -> evalFuncCall(expr).flatMap(NumericValues::narrow);
            case AlgebraicParser.VariableAtomContext expr -> evalVariable(expr).flatMap(NumericValues::narrow);
            default -> Eval.raiseError(new EvalError.SyntaxError("Unsupported atom node: " + ctx.getText()));
        };
    }

    private Eval<NumericValue> evalFactor(AlgebraicParser.FactorContext ctx) {
        return Folds.foldLeftAssociativeNumeric(ctx.unary(), ctx.mulOp(), this::evalNumericNode);
    }
}
