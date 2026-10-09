package com.nokhrin.nolang.common.combinators;

import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.operations.BinaryNumericOperation;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Folds {
    private Folds() {
        throw new UnsupportedOperationException("Utility class");
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

    /**
     * For Algebraic, no type validation
     *
     * @param operands
     * @param operations
     * @return
     */
    public static Eval<NumericValue> foldLeftNumeric(
        List<Eval<NumericValue>> operands, List<BinaryNumericOperation> operations) {
        if (operands.isEmpty()) {
            return Eval.raiseError(new EvalError.SyntaxError("Binary expression without operands"));
        }
        if (operands.size() != operations.size() + 1) {
            return Eval.raiseError(
                new EvalError.SyntaxError("Count of operands and operations is invalid"));
        }

        Eval<NumericValue> accumulator = operands.getFirst();

        for (int i = 0; i < operations.size(); i++) {
            BinaryNumericOperation operation = operations.get(i);
            Eval<NumericValue> rightEval = operands.get(i + 1);
            accumulator = operation.apply(accumulator, rightEval);
        }
        return accumulator;
    }

    public static Eval<Value> foldLeftAssociativeNumeric(
        List<? extends ParserRuleContext> operandCtx,
        List<? extends ParserRuleContext> operationCtx,
        Function<? super ParserRuleContext, Eval<Value>> evalFunction
    ) {
        if (operandCtx.isEmpty()) {
            return Eval.raiseError(new EvalError.SyntaxError("Expression is empty"));
        }
        List<Eval<Value>> operands = operandCtx.stream().map(evalFunction).toList();
        List<BinaryNumericOperation> operations = new ArrayList<>();

        for (ParserRuleContext operation : operationCtx) {
            Either<EvalError, BinaryNumericOperation> parsed = BinaryNumericOperation.fromSymbol(operation.getText());
            if (parsed.isLeft()) {
                return Eval.raiseError(parsed.leftOptional().orElseThrow());
            }
            operations.add(parsed.rightOptional().orElseThrow());
        }

        if (operations.isEmpty()) {
            return operands.getFirst(); //single operand in expression
        }

        Eval<NumericValue> accumulator = operands.getFirst().flatMap(ValueCombinators::narrowToNumericValue);
        for (int i = 0; i < operations.size(); i++) {
            BinaryNumericOperation operation = operations.get(i);
            Eval<Value> operandValue = operands.get(i + 1);
            Eval<NumericValue> operand = operandValue.flatMap(ValueCombinators::narrowToNumericValue);
            accumulator = operation.apply(accumulator, operand);
        }
        return EvalCombinators.upcastToValue(accumulator);
    }


    public static Eval<List<Value>> collectArguments(List<? extends Eval<? extends Value>> argsEval) {
        Eval<List<Value>> accumulator = Eval.pure(List.of());
        for (Eval<? extends Value> arg : argsEval) {
            accumulator =
                accumulator.flatMap(
                    argsInitial ->
                        arg.map(
                            value -> {
                                List<Value> updatedArgs = new ArrayList<>(argsInitial.size() + 1);
                                updatedArgs.addAll(argsInitial);
                                updatedArgs.add(value);
                                return List.copyOf(updatedArgs);
                            }));
        }
        return accumulator;
    }
}
