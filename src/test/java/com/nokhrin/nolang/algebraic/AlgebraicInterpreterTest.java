package com.nokhrin.nolang.algebraic;

import static org.testng.Assert.assertEquals;

import com.nokhrin.nolang.common.executions.Result;
import com.nokhrin.nolang.common.executions.ValueResult;
import com.nokhrin.nolang.common.values.IntValue;
import java.io.OutputStream;
import java.io.PrintStream;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.arbitraries.StringArbitrary;

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
    assertEquals(result, new ValueResult(new IntValue(varValue)));
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
    assertEquals(interpreter.evaluate(reassignment), new ValueResult(new IntValue(val2)));
  }
}
