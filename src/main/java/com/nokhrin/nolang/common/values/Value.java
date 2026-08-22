package com.nokhrin.nolang.common.values;

public sealed interface Value permits NumericValue, BoolValue {
  default NumericValue asNumeric() {
    return switch (this) {
      case NumericValue numericValue -> numericValue;
      case BoolValue boolValue ->
          throw new IllegalStateException("Expected numeric, got boolean: " + boolValue);
    };
  }
}
