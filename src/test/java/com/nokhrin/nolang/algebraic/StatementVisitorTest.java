package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.core.Scope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatementVisitorTest {
    @Test
    void visitStatement_delegates_scopeAndResultsNotModified() {
        Scope scopeInitial = new Scope();


        Scope scopeAfterVisit = new Scope();


        assertAll(
            () -> assertEquals(scopeInitial, scopeAfterVisit)
        );

    }
}
