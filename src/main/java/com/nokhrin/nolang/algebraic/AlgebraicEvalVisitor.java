package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicBaseVisitor;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.functional.EnvironmentCombinators;
import com.nokhrin.nolang.common.functional.Folds;
import com.nokhrin.nolang.common.functional.ScopeCombinators;
import com.nokhrin.nolang.common.operations.BinaryNumericOperation;
import com.nokhrin.nolang.common.operations.UnaryNumericOperation;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.functional.ValueParser;
import org.antlr.v4.runtime.tree.ParseTree;

public class AlgebraicEvalVisitor extends AlgebraicBaseVisitor<Eval<Value>> {
    private final AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();

    @Override
    public Eval<Value> visitProgramWithStatements(AlgebraicParser.ProgramWithStatementsContext ctx) {
        Eval<Value> accumulator = Eval.pure(Value.Void.INSTANCE);
        for (AlgebraicParser.StatementContext statement : ctx.statement()) {
            accumulator = accumulator.flatMap(_ -> visit(statement));
        }
        return accumulator;
    }

    @Override
    public Eval<Value> visitEmptyProgram(AlgebraicParser.EmptyProgramContext ctx) {
        return Eval.pure(Value.Void.INSTANCE);
    }

    @Override
    public Eval<Value> visitStatement(AlgebraicParser.StatementContext ctx) {
        return visit(ctx.assignment());
    }

    @Override
    public Eval<Value> visitAssignStatement(AlgebraicParser.AssignStatementContext ctx) {
        String varName = ctx.ID().getText();
        return visit(ctx.term())
            .flatMap(varValue ->
                ScopeCombinators.modifyScope(scope ->
                    scope.assignOrDefine(varName, varValue)));
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
            .flatMap(env -> env.scope()
                .lookup(varName)
                .fold(Eval::raiseError, Eval::pure));
    }

    //region NUMERIC

    private Eval<NumericValue> evalTerm(AlgebraicParser.TermContext ctx) {
        return Folds.foldLeftAssociativeNumeric(ctx.factor(), ctx.addOp(), this::evalNumericNode);
    }

    private Eval<Value> evalFuncCall(AlgebraicParser.FuncCallAtomContext ctx) {
        return null;
    }

    private Eval<NumericValue> evalPostfix(AlgebraicParser.PostfixContext ctx) {
        Eval<NumericValue> accumulator = evalAtom(ctx.atom());
        for (AlgebraicParser.PostfixOpContext postfixOp : ctx.postfixOp()) {
            accumulator = accumulator.flatMap(number ->
                UnaryNumericOperation.fromSymbol(postfixOp.getText())
                    .fold(Eval::raiseError,
                        operation -> operation.apply(number)));
        }
        return accumulator;
    }


    private Eval<NumericValue> evalNumericNode(ParseTree tree) {
        return switch (tree) {
            case AlgebraicParser.TermContext expr -> evalTerm(expr);
            case AlgebraicParser.FactorContext expr -> evalFactor(expr);
            case AlgebraicParser.UnaryContext expr -> evalUnary(expr);
            case AlgebraicParser.PostfixContext expr -> evalPostfix(expr);
            case AlgebraicParser.AtomContext expr -> evalAtom(expr);
            default -> Eval.raiseError(new EvalError.SyntaxError("Unsupported numeric node: " + tree));
        };
    }

    private Eval<NumericValue> evalUnary(AlgebraicParser.UnaryContext ctx) {
        return switch (ctx) {
            case AlgebraicParser.UnaryExpressionContext expr ->
                UnaryNumericOperation.fromSymbol(expr.unaryOp().getText())
                    .fold(Eval::raiseError,
                        operation -> operation.apply(evalUnary(expr.unary()))
                            .flatMap(operand -> operation.apply(operand)));
            case AlgebraicParser.PowerExpressionContext expr -> evalPostfix(expr.postfix())
                .flatMap(base -> evalUnary(expr.unary())
                    .flatMap(exponent -> BinaryNumericOperation.POW.apply(base, exponent)));
            case AlgebraicParser.PostfixExpressionContext expr -> evalPostfix(expr.postfix());
            default -> Eval.raiseError(new EvalError.SyntaxError("Unsupported unary node: " + ctx.getText()));
        };
    }

    private Eval<NumericValue> evalAtom(AlgebraicParser.AtomContext ctx) {
        return switch (ctx) {
            case AlgebraicParser.AbsoluteAtomContext expr -> evalTerm(expr.term())
                .flatMap(operand -> UnaryNumericOperation.ABSOLUTE.apply(operand));
            case AlgebraicParser.ParenthesesAtomContext expr -> evalTerm(expr.term());
            case AlgebraicParser.NumberAtomContext expr -> ValueParser.parseNumber(expr.NUM().getText());
            case AlgebraicParser.FuncCallAtomContext expr -> evalFuncCall(expr)
                .flatMap(this::narrowToNumeric);
            case AlgebraicParser.VariableAtomContext expr -> evalVariable(expr)
                .flatMap(this::narrowToNumeric);
            default -> Eval.raiseError(new EvalError.SyntaxError("Unsupported atom node: " + ctx.getText()));
        };
    }

    private Eval<NumericValue> narrowToNumeric(Value value) {
        return value.match(
            Eval::pure,
            boolValue -> Eval.raiseError(new EvalError.TypeError("Numeric expected, got boolean: " + boolValue)),
            voidValue -> Eval.raiseError(new EvalError.TypeError("Numeric expected, got boolean: " + voidValue))
        );
    }

    private Eval<NumericValue> evalFactor(AlgebraicParser.FactorContext ctx) {
        return Folds.foldLeftAssociativeNumeric(ctx.unary(), ctx.mulOp(), this::evalNumericNode);
    }
}

