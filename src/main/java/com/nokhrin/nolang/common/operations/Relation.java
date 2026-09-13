package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.values.Value;

public interface Relation<L, R> {
    Eval<Value.Bool> apply(L left, R right);

    default Eval<Value.Bool> apply(Eval<L> left, Eval<R> right) {
        return left.flatMap(l -> right.flatMap(r -> apply(l, r)));
    }
}
