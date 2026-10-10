package com.nokhrin.nolang.common;

import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

public sealed interface Type
        permits Type.BoolType, Type.IntType, Type.NumericType, Type.RealType, Type.VoidType {
    boolean accepts(Value value);

    record NumericType() implements Type {
        public boolean accepts(Value v) {
            return v instanceof NumericValue;
        }
    }

    record IntType() implements Type {
        public boolean accepts(Value v) {
            return v instanceof NumericValue.IntValue;
        }
    }

    record RealType() implements Type {
        public boolean accepts(Value v) {
            return v instanceof NumericValue.RealValue;
        }
    }

    record BoolType() implements Type {
        public boolean accepts(Value v) {
            return v instanceof Value.BoolValue;
        }
    }

    record VoidType() implements Type {
        public boolean accepts(Value v) {
            return v instanceof Value.VoidValue;
        }
    }
}
