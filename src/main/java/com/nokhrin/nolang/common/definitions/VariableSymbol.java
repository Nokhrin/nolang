package com.nokhrin.nolang.common.definitions;

import com.nokhrin.nolang.common.Type;

public record VariableSymbol(String name, Type type) implements Symbol {
}
