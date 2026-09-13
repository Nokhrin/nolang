package com.nokhrin.nolang.common;

public sealed interface Type permits Type.Bool, Type.Int, Type.Numeric, Type.Real, Type.Void {
    record Numeric() implements Type {
    }

    record Int() implements Type {
    }

    record Real() implements Type {
    }

    record Bool() implements Type {
    }

    record Void() implements Type {
    }
}
