package com.nokhrin.nolang.common.definitions;

import com.nokhrin.nolang.common.Type;

import java.util.Optional;

public record ParameterSymbol(
        String name,
        Optional<Type> type
) {
}
