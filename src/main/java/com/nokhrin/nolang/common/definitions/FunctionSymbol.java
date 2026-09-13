package com.nokhrin.nolang.common.definitions;

import com.nokhrin.nolang.common.Type;

import java.util.List;

public record FunctionSymbol(String name, List<FunctionParameter> formalParameters, Type returnType)
    implements Symbol {
}
