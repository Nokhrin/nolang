package com.nokhrin.nolang.common.operations;

import static com.nokhrin.nolang.common.operations.NumericComparisonOperation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.core.FunctionRegistry;
import com.nokhrin.nolang.common.core.Scope;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NumericComparisonOperationTest {
    private final ExecutionContext context =
            new ExecutionContext(new Scope(), new FunctionRegistry(Map.of()), List.of());

    static Stream<Arguments> validComparisonCases() {
        return Stream.of(
                Arguments.of(
                        new NumericValue.IntValue(5),
                        GT,
                        new NumericValue.IntValue(3),
                        new Value.BoolValue(true)),
                Arguments.of(
                        new NumericValue.IntValue(3),
                        GT,
                        new NumericValue.IntValue(5),
                        new Value.BoolValue(false)),
                Arguments.of(
                        new NumericValue.IntValue(5),
                        EQ,
                        new NumericValue.IntValue(5),
                        new Value.BoolValue(true)),
                Arguments.of(
                        new NumericValue.IntValue(5),
                        NEQ,
                        new NumericValue.IntValue(3),
                        new Value.BoolValue(true)),
                Arguments.of(
                        new NumericValue.IntValue(5),
                        GEQ,
                        new NumericValue.IntValue(5),
                        new Value.BoolValue(true)),
                Arguments.of(
                        new NumericValue.IntValue(5),
                        LEQ,
                        new NumericValue.IntValue(3),
                        new Value.BoolValue(false)),
                Arguments.of(
                        new NumericValue.RealValue(5.5),
                        GT,
                        new NumericValue.RealValue(3.5),
                        new Value.BoolValue(true)),
                Arguments.of(
                        new NumericValue.IntValue(5),
                        GT,
                        new NumericValue.RealValue(3.5),
                        new Value.BoolValue(true)),
                Arguments.of(
                        new NumericValue.IntValue(5),
                        EQ,
                        new NumericValue.RealValue(5.0),
                        new Value.BoolValue(true)));
    }

    @ParameterizedTest
    @MethodSource("validComparisonCases")
    public void compare_validOperands_expectedResult(
            NumericValue left,
            NumericComparisonOperation op,
            NumericValue right,
            Value.BoolValue expected) {

        Eval<NumericValue> leftEval = Eval.pure(left);
        Eval<NumericValue> rightEval = Eval.pure(right);
        Eval<Value.BoolValue> expectedEval = Eval.pure(expected);

        assertEquals(
                expectedEval.run(context),
                leftEval.flatMap(lBool -> rightEval.flatMap(rBool -> compare(lBool, op, rBool)))
                        .run(context));
    }
}
