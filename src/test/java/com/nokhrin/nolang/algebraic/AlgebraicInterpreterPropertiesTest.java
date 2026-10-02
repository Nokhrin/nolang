package com.nokhrin.nolang.algebraic;

import static org.junit.jupiter.api.Assertions.*;

import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import java.util.List;
import java.util.Map;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.arbitraries.StringArbitrary;

public class AlgebraicInterpreterPropertiesTest {
  AlgebraicInterpreter interpreter;

  private final ExecutionContext context =
      new ExecutionContext(new Scope(), new FunctionRegistry(Map.of()), List.of());

  @Provide("validVarNames")
  StringArbitrary validVarNames() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10);
  }

  @Property
  void variablesPreserveValue(@ForAll("validVarNames") String varName, @ForAll int varValue) {
    interpreter = AlgebraicInterpreter.create();
    String declaration = varName + "=" + varValue + "\n" + varName;
    EvalResult<Value> actual = interpreter.evaluate(declaration, context);

    switch (actual) {
      case EvalResult.Returned<Value> returned -> {
        assertAll(
            () -> assertInstanceOf(NumericValue.IntValue.class, returned.value()),
            () -> assertEquals(new NumericValue.IntValue(varValue), returned.value()),
            () ->
                assertEquals(
                    Either.right(new NumericValue.IntValue(varValue)),
                    returned.executionContext().scope().lookup(varName)));
      }
      case EvalResult.Interrupted<Value> interrupted ->
          fail("Unexpected interruption: " + interrupted.reason().message());
    }
  }

  @Property
  void variableReassigned_assignedValueMatch(@ForAll int valInitial, @ForAll int valFinal) {
    interpreter = AlgebraicInterpreter.create();

    String reassignment =
        """
                x = %d
                x = %d
                x
                """
            .formatted(valInitial, valFinal);

    EvalResult<Value> actual = interpreter.evaluate(reassignment, context);

    switch (actual) {
      case EvalResult.Returned<Value> returned -> {
        assertEquals(new NumericValue.IntValue(valFinal), returned.value());
      }
      case EvalResult.Interrupted<Value> interrupted ->
          fail("Unexpected interruption: " + interrupted.reason().message());
    }
  }
}
