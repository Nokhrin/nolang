package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.core.FunctionRegistry;
import com.nokhrin.nolang.common.core.Scope;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

public abstract class AlgebraicIntegrationTestBase {
    ExecutionContext context;
    AlgebraicInterpreter interpreter;

    @BeforeEach
    public void setUp() {
        Scope scope = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        List<String> stdout = List.of();
        context = new ExecutionContext(scope, registry, stdout);
        interpreter = AlgebraicInterpreter.create();
    }

}
