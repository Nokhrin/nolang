package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.values.Value;
import java.util.Map;

record ProgramTestCase(
        String id, String source, Value expectedResult, Map<String, Value> expectedScope) {
    @Override
    public String toString() {
        return id;
    }
}
