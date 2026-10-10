package com.nokhrin.nolang.algebraic;

import static org.junit.jupiter.api.Assertions.*;

import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import java.util.List;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.arbitraries.StringArbitrary;
import org.junit.jupiter.api.Disabled;

public class AlgebraicInterpreterPropertiesTest {
    private final AlgebraicInterpreter interpreter;
    private final ExecutionContext context;

    public AlgebraicInterpreterPropertiesTest() {
        this.interpreter = AlgebraicInterpreter.create();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        Scope scope = new Scope();
        this.context = new ExecutionContext(scope, registry, List.of());
    }

    @Provide("validVarNames")
    StringArbitrary validVarNames() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10);
    }

    @Property
    void variablesPreserveValue(@ForAll("validVarNames") String varName, @ForAll int varValue) {
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

    @Disabled("long min value need BigInteger to be parsed, to be added in dynamic stage")
    @Property
    void integersParsedAsIntValues(@ForAll long number) {
        String src = String.valueOf(number);
        EvalResult<Value> result = interpreter.evaluate(src, context);
        switch (result) {
            case EvalResult.Returned<Value> actual ->
                    assertInstanceOf(NumericValue.IntValue.class, actual.value());
            case EvalResult.Interrupted<Value> interrupted ->
                    fail("Unexpected interruption: " + interrupted);
        }
    }

    @Disabled("scientific notation to be added in dynamic stage")
    @Property
    void realsParsedAsRealValues(@ForAll double number) {
        String src = String.valueOf(number);
        EvalResult<Value> result = interpreter.evaluate(src, context);
        switch (result) {
            case EvalResult.Returned<Value> actual ->
                    assertInstanceOf(NumericValue.RealValue.class, actual.value());
            case EvalResult.Interrupted<Value> interrupted ->
                    fail("Unexpected interruption: " + interrupted);
        }
    }
}
