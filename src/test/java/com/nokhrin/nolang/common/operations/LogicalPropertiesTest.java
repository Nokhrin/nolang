package com.nokhrin.nolang.common.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.core.FunctionRegistry;
import com.nokhrin.nolang.common.core.Scope;
import com.nokhrin.nolang.common.values.Value;
import java.util.List;
import java.util.Map;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

public class LogicalPropertiesTest {
  private final ExecutionContext context =
      new ExecutionContext(new Scope(), new FunctionRegistry(Map.of()), List.of());

  /** and(a, a) == a */
  @Property
  void idempotence_and(@ForAll boolean a) {
    Value.BoolValue aValue = new Value.BoolValue(a);
    Eval<Value.BoolValue> leftEval = Logical.and(aValue, aValue);
    Eval<Value.BoolValue> rightEval = Eval.pure(aValue);

    assertEquals(rightEval.run(context), leftEval.run(context));
  }

  /** or(a, a) == a */
  @Property
  void idempotence_or(@ForAll boolean a) {
    Value.BoolValue aValue = new Value.BoolValue(a);
    Eval<Value.BoolValue> leftEval = Logical.or(aValue, aValue);
    Eval<Value.BoolValue> rightEval = Eval.pure(aValue);

    assertEquals(rightEval.run(context), leftEval.run(context));
  }

  /** not(and(a, b)) == or(not(a), not(b)) */
  @Property
  void deMorgan_law_and(@ForAll boolean a, @ForAll boolean b) {
    Value.BoolValue aValue = new Value.BoolValue(a);
    Value.BoolValue bValue = new Value.BoolValue(b);
    Eval<Value.BoolValue> leftEval = Logical.and(aValue, bValue).flatMap(Logical::not);
    Eval<Value.BoolValue> rightEval =
        Logical.not(aValue)
            .flatMap(aNot -> Logical.not(bValue).flatMap(bNot -> Logical.or(aNot, bNot)));

    assertEquals(leftEval.run(context), rightEval.run(context));
  }

  /** not(or(a, b)) == and(not(a), not(b)) */
  @Property
  void deMorgan_law_or(@ForAll boolean a, @ForAll boolean b) {
    Value.BoolValue aValue = new Value.BoolValue(a);
    Value.BoolValue bValue = new Value.BoolValue(b);
    Eval<Value.BoolValue> leftEval = Logical.or(aValue, bValue).flatMap(Logical::not);
    Eval<Value.BoolValue> rightEval =
        Logical.not(aValue)
            .flatMap(aNot -> Logical.not(bValue).flatMap(bNot -> Logical.and(aNot, bNot)));

    assertEquals(leftEval.run(context), rightEval.run(context));
  }

  /** not(not(a)) == a */
  @Property
  void double_neg(@ForAll boolean a) {
    Value.BoolValue aValue = new Value.BoolValue(a);
    Eval<Value.BoolValue> leftEval = Logical.not(aValue).flatMap(Logical::not);
    Eval<Value.BoolValue> rightEval = Eval.pure(aValue);

    assertEquals(leftEval.run(context), rightEval.run(context));
  }

  /** and(a, b) == and(b, a) */
  @Property
  void commutativity(@ForAll boolean a, @ForAll boolean b) {
    Value.BoolValue aValue = new Value.BoolValue(a);
    Value.BoolValue bValue = new Value.BoolValue(b);
    Eval<Value.BoolValue> leftEval = Logical.and(aValue, bValue);
    Eval<Value.BoolValue> rightEval = Logical.and(bValue, aValue);

    assertEquals(leftEval.run(context), rightEval.run(context));
  }

  /** and(a, or(a, b)) == a */
  @Property
  void absorption(@ForAll boolean a, @ForAll boolean b) {
    Value.BoolValue aValue = new Value.BoolValue(a);
    Value.BoolValue bValue = new Value.BoolValue(b);
    Eval<Value.BoolValue> leftEval =
        Eval.pure(aValue)
            .flatMap(
                aEval -> Logical.or(aValue, bValue).flatMap(orEval -> Logical.and(aEval, orEval)));
    Eval<Value.BoolValue> rightEval = Eval.pure(aValue);

    assertEquals(leftEval.run(context), rightEval.run(context));
  }
}
