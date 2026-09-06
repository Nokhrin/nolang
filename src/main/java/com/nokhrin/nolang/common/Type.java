package com.nokhrin.nolang.common;

public sealed interface Type permits Type.BoolType, Type.IntType, Type.RealType, Type.VoidType {
    record IntType() implements Type {
    }

    record RealType() implements Type {
    }

    record BoolType() implements Type {
    }

    record VoidType() implements Type {
    }
}
