package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.values.NumericValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

public class NumberVariableIntegrationTest {
    @Test
    void defineRedefineRead_redefinedReturned() {
        String src = """
            x=1
            x=2
            x
            """;
        NumericValue.IntValue expectedVal = new NumericValue.IntValue(2);
        assertAll(
            //eval(src)->Returned.class
            //eval(src)->x=2
            //eval(src)->scope contains x=2
        );
    }

    @Test
    void readUndefined_undefinedErrorReturned() {
        String src = """
            undefinedVar
            """;

        assertAll(
            //eval(src)->
            // Interrupted.class
            // InterruptReason.Error
            // ScopeError.UndefinedVariable
        );
    }
}
