package com.nokhrin.nolang.common.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.core.FunctionRegistry;
import com.nokhrin.nolang.common.core.Scope;
import com.nokhrin.nolang.common.values.NumericValue;
import java.util.List;
import java.util.Map;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

public class NumericPropertiesTest {
  private final ExecutionContext context =
      new ExecutionContext(new Scope(), new FunctionRegistry(Map.of()), List.of());

  @Property
  void integerAdditionIsCommutative(@ForAll long a, @ForAll long b) {
    NumericValue numA = new NumericValue.IntValue(a);
    NumericValue numB = new NumericValue.IntValue(b);

    // (a+b)=(b+a)
    Eval<NumericValue> leftEval = Numeric.add(numA, numB);
    Eval<NumericValue> rightEval = Numeric.add(numB, numA);

    assertEquals(leftEval.run(context), rightEval.run(context));
  }

  @Property
  void integerMultiplicationIsCommutative(@ForAll long a, @ForAll long b) {
    NumericValue numA = new NumericValue.IntValue(a);
    NumericValue numB = new NumericValue.IntValue(b);

    // (a*b)=(b*a)
    Eval<NumericValue> leftEval = Numeric.mul(numA, numB);
    Eval<NumericValue> rightEval = Numeric.mul(numB, numA);

    assertEquals(leftEval.run(context), rightEval.run(context));
  }

  @Property
  void integerAdditionIdentity(@ForAll long a) {
    NumericValue numA = new NumericValue.IntValue(a);
    NumericValue numZero = new NumericValue.IntValue(0);

    // (a+0)=a
    Eval<NumericValue> leftEval = Numeric.add(numA, numZero);

    assertEquals(leftEval.run(context), Eval.pure(numA).run(context));
  }

  @Property
  void integerMultiplicationIdentity(@ForAll long a) {
    NumericValue numA = new NumericValue.IntValue(a);
    NumericValue numOne = new NumericValue.IntValue(1);

    // (a*1)=a
    Eval<NumericValue> leftEval = Numeric.mul(numA, numOne);

    assertEquals(leftEval.run(context), Eval.pure(numA).run(context));
  }

  @Property
  void integerSubtractionInverse(@ForAll long a) {
    NumericValue numA = new NumericValue.IntValue(a);
    NumericValue numZero = new NumericValue.IntValue(0);

    // a-a
    Eval<NumericValue> eval = Numeric.sub(numA, numA);
    assertEquals(eval.run(context), Eval.pure(numZero).run(context));
  }

  @Property
  void realAdditionIsCommutative(@ForAll long a, @ForAll long b) {
    if (Double.isInfinite(a) || Double.isInfinite(b)) {
      return;
    }
    NumericValue numA = new NumericValue.RealValue(a);
    NumericValue numB = new NumericValue.RealValue(b);

    // (a+b)=(b+a)
    Eval<NumericValue> leftEval = Numeric.add(numA, numB);
    Eval<NumericValue> rightEval = Numeric.add(numB, numA);

    assertEquals(leftEval.run(context), rightEval.run(context));
  }
}
