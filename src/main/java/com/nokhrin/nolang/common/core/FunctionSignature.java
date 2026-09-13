package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.definitions.FunctionParameter;

import java.util.List;

public record FunctionSignature(
    String name,
    List<FunctionParameter> parameters,
    Type returnType,
    Arity arity
) {
}
