package com.nokhrin.nolang.common.values;

import java.util.Optional;

public sealed interface NumericValue extends Value permits IntValue, RealValue {
}
