package com.nokhrin.nolang.common.core;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.values.Value;
import java.util.List;

public sealed interface Declaration
        permits Declaration.Parameter, Declaration.Function, Declaration.Variable {
    String name();

    record Variable(String name, Type type) implements Declaration {}

    record Function(String name, List<Parameter> formalParameters, Type returnType)
            implements Declaration {}

    record Parameter(String name, Type type) implements Declaration {
        public boolean accepts(Value value) {
            return type.accepts(value);
        }
    }
}
