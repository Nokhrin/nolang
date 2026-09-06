package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.definitions.ParameterSymbol;

import java.util.List;

public record FunctionSignature(String name, List<ParameterSymbol> parameters, Type returnType) {
}
