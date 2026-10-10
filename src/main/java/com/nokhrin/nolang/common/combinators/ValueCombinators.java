package com.nokhrin.nolang.common.combinators;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

public class ValueCombinators {
    public static Eval<NumericValue> narrowToNumericValue(Value value) {
        return value.match(
                Eval::pure,
                boolVal ->
                        Eval.raiseError(
                                new EvalError.TypeError(
                                        "Numeric expected, got boolean: " + boolVal)),
                voidVal ->
                        Eval.raiseError(
                                new EvalError.TypeError("Numeric expected, got: " + voidVal)));
    }
}
