package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.values.NumericValue;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.arbitraries.StringArbitrary;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlgebraicInterpreterTest {

    @Provide("validIds")
    StringArbitrary validIds() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10);
    }

    @Property
    void variablesPreserveValue(@ForAll("validIds") String varId, @ForAll int varValue) {
        PrintStream nullOutput = new PrintStream(OutputStream.nullOutputStream());
        AlgebraicInterpreter interpreter = new AlgebraicInterpreter(nullOutput);
        String declaration = varId + "=" + varValue + "\n" + varId;
        Result result = interpreter.evaluate(declaration);
        assertEquals(result, new ValueResult(new NumericValue.Int(varValue)));
    }

    @Property
    void variableReassigned_assignedValueMatch(@ForAll int val1, @ForAll int val2) {
        PrintStream nullOutput = new PrintStream(OutputStream.nullOutputStream());
        AlgebraicInterpreter interpreter = new AlgebraicInterpreter(nullOutput);

        String reassignment =
            """
                x = %d
                x = %d
                x
                """
                .formatted(val1, val2);
        assertEquals(interpreter.evaluate(reassignment), new ValueResult(new NumericValue.Int(val2)));
    }
}
