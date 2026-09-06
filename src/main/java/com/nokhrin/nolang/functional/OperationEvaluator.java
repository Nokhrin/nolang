package com.nokhrin.nolang.functional;

import com.nokhrin.nolang.common.operations.BinaryNumericOperation;
import com.nokhrin.nolang.common.operations.UnaryNumericOperation;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

public class OperationEvaluator {
    public static Eval<Value> apply(UnaryNumericOperation operation, Value value) {
        if (value instanceof NumericValue number) {
            return operation.apply(number).map(result -> result);
        }
        return Eval.raiseError(new EvalError.TypeError("Expected numeric value, got: " + value));
    }

    public static Eval<Value> apply(BinaryNumericOperation operation, Value left, Value right) {
        if (left instanceof NumericValue lNum && right instanceof NumericValue rNum) {
            return operation.apply(lNum, rNum).map(result -> result);
        }
        return Eval.raiseError(new EvalError.TypeError("Expected numeric values, got left: " + left + ", right: " + right));
    }

}
