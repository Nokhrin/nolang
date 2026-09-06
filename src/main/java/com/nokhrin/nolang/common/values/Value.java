package com.nokhrin.nolang.common.values;

import java.util.Optional;
import java.util.function.Function;

public sealed interface Value permits NumericValue, BoolValue, VoidValue {
    <T> T match(
        Function<NumericValue, T> onNumeric,
        Function<BoolValue, T> onBool,
        Function<VoidValue, T> onVoid
    );

    default Optional<NumericValue> asNumeric() {
        return match(
            Optional::of,
            _ -> Optional.empty(),
            _ -> Optional.empty()
        );
    }

    default Optional<BoolValue> asBool() {
        return match(
            _ -> Optional.empty(),
            Optional::of,
            _ -> Optional.empty()
        );
    }

    default Optional<VoidValue> asVoid() {
        return match(
            _ -> Optional.empty(),
            _ -> Optional.empty(),
            Optional::of
        );
    }
}
