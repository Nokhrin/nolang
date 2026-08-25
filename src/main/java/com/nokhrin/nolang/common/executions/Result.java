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

    default BoolValue asBoolValue() {
        return switch (asValue()) {
            case BoolValue boolValue -> boolValue;
            case NumericValue numericValue ->
                throw new IllegalStateException("Expected boolean, got numeric: " + numericValue);
        };
    }

    default NumericValue asNumericValue() {
        return switch (asValue()) {
            case NumericValue numericValue -> numericValue;
            case BoolValue boolValue -> throw new IllegalStateException("Expected numeric, got boolean: " + boolValue);
        };
    }
}
