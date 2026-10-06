package com.nokhrin.nolang.algebraic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

public class AssignmentVisitorTest {
    @Test
    void varDefined_scopeWithVar_voidReturned() {
        String src = """
            x=1
            """;

        assertAll(

        );

    }

    @Test
    void varRedefined_scopeWithUpdatedVar_voidReturned() {
        String src = """
            x=1
            x=2
            """;
    }

    @Test
    void malformedDefinition_scopeWithoutVar_errorReturned() {
        String src = """
            x=1/0
            """;
    }

    @Test
    void defineWithVarNotInScope_scopeWithoutVar_errorReturned() {
        String src = """
            x=undefined_var
            """;
    }
}
