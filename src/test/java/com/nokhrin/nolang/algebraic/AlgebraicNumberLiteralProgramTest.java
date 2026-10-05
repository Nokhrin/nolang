package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.core.EvalResult;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AlgebraicNumberLiteralProgramTest extends AlgebraicIntegrationTestBase {

    static Stream<Arguments> validNumberAtoms() {
        return Stream.of(
            Arguments.of("0", new NumericValue.IntValue(0)),
            Arguments.of("1", new NumericValue.IntValue(1)),
            Arguments.of("123", new NumericValue.IntValue(123)),
            Arguments.of(".5", new NumericValue.RealValue(0.5)),
            Arguments.of("5.", new NumericValue.RealValue(5.0)),
            Arguments.of("1.25", new NumericValue.RealValue(1.25))
        );
    }

    @ParameterizedTest
    @MethodSource("validNumberAtoms")
    void evaluateArithmetic_actualEqualsExpected(String input, NumericValue expected) {
        EvalResult<Value> actual = interpreter.evaluate(input, context);
        switch (actual) {
            case EvalResult.Returned<Value> returned -> assertEquals(expected, returned.value());
            case EvalResult.Interrupted<Value> interrupted ->
                fail("Interrupted, reason: " + interrupted.reason().message());
        }
    }
}
