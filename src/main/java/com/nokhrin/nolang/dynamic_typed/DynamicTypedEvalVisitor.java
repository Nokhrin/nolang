package com.nokhrin.nolang.dynamic_typed;

import com.nokhrin.nolang.DynamicTypedBaseVisitor;
import com.nokhrin.nolang.DynamicTypedParser;
import com.nokhrin.nolang.DynamicTypedParser.*;
import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.errors.SemanticException;
import com.nokhrin.nolang.common.executions.*;
import com.nokhrin.nolang.common.operations.BinaryOperation;
import com.nokhrin.nolang.common.operations.Folds;
import com.nokhrin.nolang.common.operations.Logical;
import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

import static com.nokhrin.nolang.common.operations.Arithmetic.neg;
import static com.nokhrin.nolang.common.operations.BinaryOperation.POW;
import static com.nokhrin.nolang.common.operations.InfixOperation.ABSOLUTE;
import static com.nokhrin.nolang.common.operations.PostfixOperation.FACTORIAL;
import static com.nokhrin.nolang.common.values.ValueParser.parseBoolean;
import static com.nokhrin.nolang.common.values.ValueParser.parseNumber;

public class DynamicTypedEvalVisitor extends DynamicTypedBaseVisitor<Result> {
    private Scope scope;
    private final FunctionRegistry functionRegistry;

    public DynamicTypedEvalVisitor(Scope scope, FunctionRegistry functionRegistry) {
        this.scope = scope;
        this.functionRegistry = functionRegistry;
    }

    private List<ParameterSymbol> extractParameters(DynamicTypedParser.ParametersContext ctx) {
        if (ctx == null) {
            return List.of();
        }

        return ctx.parameter().stream()
            .map(parameter -> {
                if (parameter instanceof TypedParameterContext typedParameter) {
                    String name = typedParameter.ID().getText();
                    Type type = resolveType(typedParameter.parameterType());
                    return new ParameterSymbol(name, type);
                } else if (parameter instanceof UntypedParameterContext untypedParameter) {
                    String name = untypedParameter.ID().getText();
                    return new ParameterSymbol(name, Type.VOID);
                }
                throw new IllegalArgumentException("Unknow parameter type: " + parameter.getText());
            })
            .toList();
    }

    private Type resolveType(ParameterTypeContext ctx) {
        if (ctx.INT_TYPE() != null) {
            return Type.INTEGER;
        }
        if (ctx.REAL_TYPE() != null) {
            return Type.REAL;
        }
        if (ctx.BOOL_TYPE() != null) {
            return Type.BOOLEAN;
        }
        return Type.VOID;
    }

    private void defineUserFunction(
        String name,
        List<ParameterSymbol> parameters,
        BlockContext block
    ) {
        FunctionSignature signature = new FunctionSignature(name, parameters);
        FunctionBody body = createFunctionBody(block);
        functionRegistry.define(new UserFunction(signature, body, scope));
    }

    private FunctionBody createFunctionBody(BlockContext block) {
        return functionScope -> {
            Scope parentScope = scope;
            scope = functionScope;

            try {
                Result rawResult = visit(block);
                return unwrapFunctionResult(rawResult);
            } finally {
                scope = parentScope;
            }
        };
    }

    private Result unwrapFunctionResult(Result result) {
        if (result instanceof Return returnSignal) {
            return returnSignal.result();
        }
        if (result instanceof Break breakSignal) {
            throw new SemanticException("Break called from function");
        }
        if (result instanceof Continue continueSignal) {
            throw new SemanticException("Continue called from function");
        }
        return new VoidResult();
    }

    @Override
    public Result visitProgram(ProgramContext ctx) {
        Result lastResult = new VoidResult();
        for (DeclarationContext declaration : ctx.declaration()) {
            lastResult = visit(declaration);

            if (lastResult instanceof Break) {
                throw new SemanticException("Break called from program");
            }
            if (lastResult instanceof Continue) {
                throw new SemanticException("Continue called from program");
            }
            if (lastResult instanceof Return) {
                throw new SemanticException("Return called from program");
            }

        }
        return lastResult;
    }

    @Override
    public Result visitStatementDecl(StatementDeclContext ctx) {
        return super.visitStatementDecl(ctx);
    }

    @Override
    public Result visitParameterType(ParameterTypeContext ctx) {
        return super.visitParameterType(ctx);
    }

    @Override
    public Result visitReturnType(ReturnTypeContext ctx) {
        return super.visitReturnType(ctx);
    }

    @Override
    public Result visitTypedVarDecl(TypedVarDeclContext ctx) {
        String varName = ctx.ID().getText();
        Value varValue = visit(ctx.expression()).asValue();
        scope.upsert(varName, varValue);
        return new VoidResult();
    }

    @Override
    public Result visitUntypedVarDecl(UntypedVarDeclContext ctx) {
        String varName = ctx.ID().getText();
        Value varValue = visit(ctx.expression()).asValue();
        scope.upsert(varName, varValue);
        return new VoidResult();
    }

    @Override
    public Result visitTypedFuncWithParams(TypedFuncWithParamsContext ctx) {
        List<ParameterSymbol> parameters = extractParameters(ctx.parameters());
        defineUserFunction(ctx.ID().getText(), parameters, ctx.block());
        return new VoidResult();
    }

    @Override
    public Result visitTypedFuncNoParams(TypedFuncNoParamsContext ctx) {
        defineUserFunction(ctx.ID().getText(), List.of(), ctx.block());
        return new VoidResult();
    }

    @Override
    public Result visitUntypedFuncWithParams(UntypedFuncWithParamsContext ctx) {
        List<ParameterSymbol> parameters = extractParameters(ctx.parameters());
        defineUserFunction(ctx.ID().getText(), parameters, ctx.block());
        return new VoidResult();
    }

    @Override
    public Result visitUntypedFuncNoParams(UntypedFuncNoParamsContext ctx) {
        defineUserFunction(ctx.ID().getText(), List.of(), ctx.block());
        return new VoidResult();
    }

    @Override
    public Result visitIfElseStat(IfElseStatContext ctx) {
        BoolValue condition = visit(ctx.expression()).asBoolValue();
        if (condition.value()) {
            return visit(ctx.statement(0));
        } else {
            return visit(ctx.statement(1));
        }
    }

    @Override
    public Result visitIfStat(IfStatContext ctx) {
        BoolValue condition = visit(ctx.expression()).asBoolValue();
        if (condition.value()) {
            return visit(ctx.statement());
        }
        return new VoidResult();
    }

    @Override
    public Result visitBreakStatement(BreakStatementContext ctx) {
        return new Break();
    }

    @Override
    public Result visitContinueStatement(ContinueStatementContext ctx) {
        return new Continue();
    }

    @Override
    public Result visitWhileStatement(WhileStatementContext ctx) {
        Result lastResult = new VoidResult();
        while (visit(ctx.expression()).asBoolValue().equals(new BoolValue(true))) {
            lastResult = visit(ctx.statement());
            if (lastResult instanceof Break) {
                break;
            }
            if (lastResult instanceof Continue) {
                continue;
            }
            if (lastResult instanceof Return) {
                return lastResult;
            }
        }
        return lastResult;
    }

    @Override
    public Result visitBlock(BlockContext ctx) {
        Scope parentScope = scope;
        scope = new Scope(scope);
        try {
            Result lastResult = new VoidResult();
            for (StatementContext statement : ctx.statement()) {
                lastResult = visit(statement);
                if (lastResult instanceof ControlSignal) {
                    break;
                }
            }
            return lastResult;
        } finally {
            scope = parentScope;
        }
    }

    @Override
    public Result visitReturnValue(ReturnValueContext ctx) {
        Value value = visit(ctx.expression()).asValue();
        return new Return(new ValueResult(value));
    }

    @Override
    public Result visitReturnVoid(ReturnVoidContext ctx) {
        return new Return(new VoidResult());
    }

    @Override
    public Result visitTernaryExpr(TernaryExprContext ctx) {
        BoolValue condition = visit(ctx.logicalOr()).asBoolValue();
        if (condition.value()) {
            return visit(ctx.ternary(0));
        }
        return visit(ctx.ternary(1));
    }

    @Override
    public Result visitLogicalOr(LogicalOrContext ctx) {
        List<LogicalAndContext> operands = ctx.logicalAnd();
        BoolValue accumulator = visit(operands.getFirst()).asBoolValue();
        for (int i = 1; i < operands.size(); i++) {
            BoolValue next = visit(operands.get(i)).asBoolValue();
            accumulator = Logical.or(accumulator, next);
        }
        return new ValueResult(accumulator);
    }

    @Override
    public Result visitAndExpr(AndExprContext ctx) {
        List<ComparisonContext> operands = ctx.comparison();
        BoolValue accumulator = visit(operands.getFirst()).asBoolValue();
        for (int i = 1; i < operands.size(); i++) {
            BoolValue next = visit(operands.get(i)).asBoolValue();
            accumulator = Logical.and(accumulator, next);
        }
        return new ValueResult(accumulator);
    }

    @Override
    public Result visitComparisonExpr(ComparisonExprContext ctx) {
        NumericValue left = visit(ctx.term(0)).asNumericValue();
        NumericValue right = visit(ctx.term(1)).asNumericValue();
        String operator = ctx.getChild(1).getText();
        Logical.LogicalOperation operation = Logical.LogicalOperation.fromSymbol(operator);
        return new ValueResult(Logical.compare(left, operation, right));
    }

    @Override
    public Result visitAdditiveExpr(AdditiveExprContext ctx) {
        return visit(ctx.term());
    }

    @Override
    public Result visitMultiplicativeExpr(MultiplicativeExprContext ctx) {
        List<NumericValue> operands = ctx.factor().stream()
            .map(this::visit)
            .map(Result::asNumericValue)
            .toList();

        List<BinaryOperation> operations = ctx.children.stream()
            .filter(TerminalNode.class::isInstance)
            .map(ParseTree::getText)
            .map(BinaryOperation::fromSymbol)
            .toList();

        return new ValueResult(Folds.left(operands, operations));
    }

    @Override
    public Result visitFactor(FactorContext ctx) {
        List<NumericValue> operands = ctx.unary().stream()
            .map(this::visit)
            .map(Result::asNumericValue)
            .toList();

        List<BinaryOperation> operations = ctx.children.stream()
            .filter(TerminalNode.class::isInstance)
            .map(ParseTree::getText)
            .map(BinaryOperation::fromSymbol)
            .toList();

        return new ValueResult(Folds.left(operands, operations));
    }

    @Override
    public Result visitUnaryMinusExpression(UnaryMinusExpressionContext ctx) {
        NumericValue value = visit(ctx.unary()).asNumericValue();
        return new ValueResult(neg(value));
    }

    @Override
    public Result visitUnaryPlusExpression(UnaryPlusExpressionContext ctx) {
        NumericValue value = visit(ctx.unary()).asNumericValue();
        return new ValueResult(value);
    }

    @Override
    public Result visitUnaryNotExpression(UnaryNotExpressionContext ctx) {
        return super.visitUnaryNotExpression(ctx);
    }

    @Override
    public Result visitAssignVar(AssignVarContext ctx) {
        String varName = ctx.ID().getText();
        Value varValue = visit(ctx.assignment()).asValue();
        scope.upsert(varName, varValue);
        return new VoidResult();
    }

    @Override
    public Result visitAssignExpr(AssignExprContext ctx) {
        return visit(ctx.ternary());
    }

    @Override
    public Result visitPowerExpression(PowerExpressionContext ctx) {
        NumericValue base = visit(ctx.atom()).asNumericValue();
        NumericValue exponent = visit(ctx.unary()).asNumericValue();
        return new ValueResult(POW.apply(base, exponent));

    }

    @Override
    public Result visitAtomExpression(AtomExpressionContext ctx) {
        return visit(ctx.atom());
    }

    @Override
    public Result visitFactorialExpression(FactorialExpressionContext ctx) {
        NumericValue value = visit(ctx.atom()).asNumericValue();
        return new ValueResult(FACTORIAL.apply(value));
    }

    @Override
    public Result visitAbsoluteAtom(AbsoluteAtomContext ctx) {
        NumericValue value = visit(ctx.assignment()).asNumericValue();
        return new ValueResult(ABSOLUTE.apply(value));
    }

    @Override
    public Result visitParenthesesAtom(ParenthesesAtomContext ctx) {
        return visit(ctx.ternary());
    }

    @Override
    public Result visitCallWithArgsAtom(CallWithArgsAtomContext ctx) {
        String funcName = ctx.ID().getText();
        List<Value> args = ctx.arguments().expression().stream()
            .map(expr -> visit(expr).asValue())
            .toList();
        return functionRegistry.invokeFunction(funcName, args);
    }

    @Override
    public Result visitCallNoArgsAtom(CallNoArgsAtomContext ctx) {
        String funcName = ctx.ID().getText();
        return functionRegistry.invokeFunction(funcName, List.of());
    }

    @Override
    public Result visitFloatAtom(FloatAtomContext ctx) {
        String lexeme = ctx.FLOAT().getText();
        return new ValueResult(parseNumber(lexeme));
    }

    @Override
    public Result visitIntAtom(IntAtomContext ctx) {
        String lexeme = ctx.INT().getText();
        return new ValueResult(parseNumber(lexeme));
    }

    @Override
    public Result visitBoolAtom(BoolAtomContext ctx) {
        String lexeme = ctx.BOOL().getText();
        return new ValueResult(parseBoolean(lexeme));
    }

    @Override
    public Result visitVoidAtom(VoidAtomContext ctx) {
        return new VoidResult();
    }

    @Override
    public Result visitVariableAtom(VariableAtomContext ctx) {
        return new ValueResult(scope.fetch(ctx.ID().getText()));
    }
}
