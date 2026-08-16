package com.nokhrin.nolang.common.values;

import com.nokhrin.nolang.common.Type;

public sealed interface Value permits BoolValue, NumericValue {
  Type type();
}
