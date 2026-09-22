package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.Type;
import java.util.List;

public record FunctionSignature(
    String name, List<Declaration.Parameter> parameters, Type returnType, Arity arity) {}
