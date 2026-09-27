package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import net.jqwik.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumericPropertiesTest {
  private final ExecutionContext context = new ExecutionContext(
    new Scope(),
    new FunctionRegistry(Map.of()),
    List.of()
  );

  @Provide
  Arbitrary<Long> fullLongRange() {
    return Arbitraries.longs();
  }

  @Provide
  Arbitrary<Long> boundaryLongs() {
    return Arbitraries.of(
      Long.MIN_VALUE,
      Long.MIN_VALUE + 1,
      -1L,
      0L,
      1L,
      Long.MAX_VALUE - 1,
      Long.MAX_VALUE
    );
  }

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

  @Property(tries = 10_000)
  void distributionForAnyLong(
    @ForAll("fullLongRange") long a,
    @ForAll("fullLongRange") long b,
    @ForAll("fullLongRange") long c) {
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

  @Property(
    tries = 343,
    generation = GenerationMode.EXHAUSTIVE
  )
  void distributionForBoundaries(
    @ForAll("boundaryLongs") long a,
    @ForAll("boundaryLongs") long b,
    @ForAll("boundaryLongs") long c) {
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
