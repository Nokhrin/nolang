package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

public sealed interface Result permits ValueResult, VoidResult, ControlSignal {
    default Value asValue() {
        return switch (this) {
            case ValueResult valueResult -> valueResult.value();
            case VoidResult() -> throw new IllegalStateException("Expected value, got void");
            case ControlSignal signal -> throw new IllegalStateException("Expected value, got signal: " + signal);
        };
    }

    default NumericValue asNumericValue() {
        return switch (asValue()) {
            case NumericValue numericValue -> numericValue;
            case BoolValue boolValue -> throw new IllegalStateException("Expected numeric, got boolean: " + boolValue);
        };
    }

}
