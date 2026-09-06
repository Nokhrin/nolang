package com.nokhrin.nolang.functional;

import com.nokhrin.nolang.common.operations.BinaryNumericOperation;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Folds {
    private Folds() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Eval<Value> foldLeft(
        List<Eval<Value>> operands, List<BinaryNumericOperation> operations) {
        if (operands.isEmpty()) {
            return Eval.raiseError(new EvalError.SyntaxError("Empty binary expression"));
        }

        if (operands.size() != operations.size() + 1) {
            return Eval.raiseError(new EvalError.SyntaxError("Invalid binary expression syntax"));
        }

        Eval<Value> accumulator = operands.getFirst();

        for (int i = 0; i < operations.size(); i++) {
            BinaryNumericOperation operation = operations.get(i);
            Eval<Value> rightNum = operands.get(i + 1);
            accumulator =
                accumulator.flatMap(
                    left -> rightNum.flatMap(right -> OperationEvaluator.apply(operation, left, right)));
        }
        return accumulator;
    }

    public static NumericValue left(
        List<NumericValue> values, List<? extends BinaryOperator<NumericValue>> operations) {
        NumericValue accumResult = values.getFirst();
        for (int i = 0; i < operations.size(); i++) {
            accumResult = operations.get(i).apply(accumResult, values.get(i + 1));
        }
        return accumResult;
    }

    public static NumericValue right(
        List<NumericValue> values, List<? extends BinaryOperator<NumericValue>> operations) {
        NumericValue accumResult = values.getLast();
        for (int i = operations.size() - 1; i >= 0; i--) {
            accumResult = operations.get(i).apply(values.get(i), accumResult);
        }
        return accumResult;
    }

    public static NumericValue right(
        NumericValue operand, List<? extends UnaryOperator<NumericValue>> operations) {
        NumericValue accumResult = operand;
        for (int i = operations.size() - 1; i >= 0; i--) {
            accumResult = operations.get(i).apply(accumResult);
        }
        return accumResult;
    }

    public static Eval<Value> foldLeftAssociative(
        List<? extends ParserRuleContext> operandCtx,
        List<? extends ParserRuleContext> operatorCtx,
        Function<? super ParserRuleContext, Eval<Value>> evalFunction
    ) {
        List<Eval<Value>> operands =
            operandCtx.stream()
                .map(evalFunction)
                .toList();

        List<BinaryNumericOperation> operations =
            operatorCtx.stream()
                .map(ParseTree::getText)
                .map(BinaryNumericOperation::fromSymbol)
                .toList();

        return Folds.foldLeft(operands, operations);

    }

    public static Eval<List<Value>> collectArguments(List<Eval<Value>> argsEval) {
        Eval<List<Value>> accumulator = Eval.pure(List.of());
        for (Eval<Value> arg : argsEval) {
            accumulator = accumulator.flatMap(argsInitial ->
                arg.map(value -> {
                    List<Value> updatedArgs = new ArrayList<>(argsInitial.size() + 1);
                    updatedArgs.addAll(argsInitial);
                    updatedArgs.add(value);
                    return List.copyOf(updatedArgs);
                }));
        }
        return accumulator;
    }
}
