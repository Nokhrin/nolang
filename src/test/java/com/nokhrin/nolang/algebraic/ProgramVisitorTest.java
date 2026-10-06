package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.core.Scope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProgramVisitorTest {

    @Test
    void calculate_lastResultReturned_scopeWithoutVarsCreated() {
        String src = """
            1+2
            8
            """;


    }

    @Test
    void assign_voidResultReturned_scopeWithVarsCreated() {
        String src = """
            x=1+2
            x=8
            """;

    }

    @Test
    void assignAndCalculate_voidReturned_scopeWithVarsCreated() {
        String src = """
            x=1+2
            x+8
            """;

    }

    @Test
    void calculationInterruption_errorReturned_scopeWithFirstVarCreated() {
        String src = """
            x=1
            x/0
            x=2
            """;
    }

    @Test
    void defitionInterruption_errorReturned_scopeIsEmpty() {
        String src = """
            undefined_var
            x=1
            """;
    }

    @Test
    void visitEmptyProgram_contextNotModified() {
        Scope scopeInitial = new Scope();


        Scope scopeAfterVisit = new Scope();


        assertAll(
            () -> assertEquals(scopeInitial, scopeAfterVisit)
        );
    }
}
