package com.nokhrin.nolang.common;

import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.operations.Numeric;
import com.nokhrin.nolang.common.values.NumericValue;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumericPropertiesTest {
  private final ExecutionContext context = new ExecutionContext(
    new Scope(),
    new FunctionRegistry(Map.of()),
    List.of()
  );
//  @Property
//  void addition_is_commutative(@ForAll long a, @ForAll long b) {
//    NumericValue numA = new NumericValue.IntValue(a);
//    NumericValue numB = new NumericValue.IntValue(b);
//    assertEquals(Numeric.add(numA, numB), Numeric.add(numB, numA));
//  }
//
//  @Property
//  void multiplication_is_commutative(@ForAll long a, @ForAll long b) {
//    NumericValue numA = new NumericValue.IntValue(a);
//    NumericValue numB = new NumericValue.IntValue(b);
//    assertEquals(Numeric.mul(numA, numB), Numeric.mul(numB, numA));
//  }
//
//  @Property
//  void addition_identity(@ForAll long a) {
//    NumericValue numA = new NumericValue.IntValue(a);
//    assertEquals(Numeric.add(numA, new NumericValue.IntValue(0)), numA);
//  }
//
//  @Property
//  void multiplication_identity(@ForAll long a) {
//    NumericValue numA = new NumericValue.IntValue(a);
//    assertEquals(Numeric.mul(numA, new NumericValue.IntValue(1)), numA);
//  }
//
//  @Property
//  void subtraction_inverse(@ForAll long a) {
//    NumericValue numA = new NumericValue.IntValue(a);
//
//    // a-a
//
//    assertEquals(Numeric.sub(numA, numA), new NumericValue.IntValue(0));
//  }

  @Property
  void distribution(
    @ForAll @IntRange(min = -10000, max = 10000) long a,
    @ForAll @IntRange(min = -10000, max = 10000) long b,
    @ForAll @IntRange(min = -10000, max = 10000) long c) {
    NumericValue numA = new NumericValue.IntValue(a);
    NumericValue numB = new NumericValue.IntValue(b);
    NumericValue numC = new NumericValue.IntValue(c);

    // a*(b+c)
    Eval<NumericValue> leftEval = Numeric.add(numB, numC)
      .flatMap(sum -> Numeric.mul(numA, sum));
    // (a*b)+(a*c)
    Eval<NumericValue> rightEval = Numeric.mul(numA, numB)
      .flatMap(ab -> Numeric.mul(numA, numC)
        .flatMap(ac -> Numeric.add(ab, ac)));

    EvalResult<NumericValue> leftResult = leftEval.run(context);
    EvalResult<NumericValue> rightResult = rightEval.run(context);

    assertEquals(leftResult, rightResult);
  }
//
//  @Property
//  void real_addition_is_commutative(@ForAll long a, @ForAll long b) {
//    if (Double.isInfinite(a) || Double.isInfinite(b)) {
//      return;
//    }
//    NumericValue numA = new NumericValue.RealValue(a);
//    NumericValue numB = new NumericValue.RealValue(b);
//    assertEquals(Numeric.add(numA, numB), Numeric.add(numB, numA));
//  }
}
