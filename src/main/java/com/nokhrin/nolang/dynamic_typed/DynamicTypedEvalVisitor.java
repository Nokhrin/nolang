package com.nokhrin.nolang.dynamic_typed;

import com.nokhrin.nolang.DynamicTypedBaseVisitor;
import com.nokhrin.nolang.DynamicTypedParser.*;
import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.values.VoidValue;
import com.nokhrin.nolang.functional.Eval;

public class DynamicTypedEvalVisitor extends DynamicTypedBaseVisitor<Eval<Value>> {

    @Override
    public Eval<Value> visitProgram(ProgramContext ctx) {
        Eval<Value> accum = Eval.pure(null);
        for (DeclarationContext declaration : ctx.declaration()) {
            accum = accum.flatMap(value -> visit(declaration));
        }
        return accum;
    }

    @Override
    public Eval<Value> visitTypedVarDecl(TypedVarDeclContext ctx) {
        return Eval.assignVariable(ctx.ID().getText(), visit(ctx.expression()));
    }

    @Override
    public Eval<Value> visitUntypedVarDecl(UntypedVarDeclContext ctx) {
        return Eval.assignVariable(ctx.ID().getText(), visit(ctx.expression()));
    }

    @Override
    public Eval<Value> visitAssignVar(AssignVarContext ctx) {
        return Eval.assignVariable(ctx.ID().getText(), visit(ctx.assignment()));
    }

    @Override
    public Eval<Value> visitBlock(BlockContext ctx) {
        return Eval.getEnvironment()
            .flatMap(
                parentEnvironment -> {
                    Scope childScope = new Scope(parentEnvironment.scope());
                    Eval<Value> blockEval =
                        Eval.modifyEnvironment(env -> env.withScope(childScope))
                            .flatMap(_ -> Eval.pure(VoidValue.INSTANCE));
                    for (StatementContext statement : ctx.statement()) {
                        blockEval = blockEval.flatMap(_ -> visit(statement));
                    }
                    return blockEval.flatMap(
                        value ->
                            Eval.modifyEnvironment(env -> env.withScope(parentEnvironment.scope()))
                                .flatMap(_ -> Eval.pure(value)));
                });
    }

    @Override
    public Eval<Value> visitIfElseStat(IfElseStatContext ctx) {
        return visit(ctx.expression())
            .flatMap(
                guard -> {
                    if (guard instanceof BoolValue b && b.value()) {
                        return visit(ctx.statement(0));
                    } else {
                        return visit(ctx.statement(1));
                    }
                });
    }

    @Override
    public Eval<Value> visitIfStat(IfStatContext ctx) {
        return visit(ctx.expression())
            .flatMap(
                guard -> {
                    if (guard instanceof BoolValue b && b.value()) {
                        return visit(ctx.statement());
                    }
                    return Eval.pure(null);
                });
    }

    @Override
    public Eval<Value> visitWhileStatement(WhileStatementContext ctx) {
        return Eval.whileLoop(visit(ctx.expression()), visit(ctx.statement()));
    }
}
