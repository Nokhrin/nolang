package com.nokhrin.nolang.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nokhrin.nolang.common.operations.Arithmetic;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.RealValue;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

public class ArithmeticPropertiesTest {
  @Property
  void addition_is_commutative(@ForAll long a, @ForAll long b) {
    NumericValue numA = new IntValue(a);
    NumericValue numB = new IntValue(b);
    assertEquals(Arithmetic.add(numA, numB), Arithmetic.add(numB, numA));
  }

  @Property
  void multiplication_is_commutative(@ForAll long a, @ForAll long b) {
    NumericValue numA = new IntValue(a);
    NumericValue numB = new IntValue(b);
    assertEquals(Arithmetic.mul(numA, numB), Arithmetic.mul(numB, numA));
  }

  @Property
  void addition_identity(@ForAll long a) {
    NumericValue numA = new IntValue(a);
    assertEquals(Arithmetic.add(numA, new IntValue(0)), numA);
  }

  @Property
  void multiplication_identity(@ForAll long a) {
    NumericValue numA = new IntValue(a);
    assertEquals(Arithmetic.mul(numA, new IntValue(1)), numA);
  }

  @Property
  void subtraction_inverse(@ForAll long a) {
    NumericValue numA = new IntValue(a);
    assertEquals(Arithmetic.sub(numA, numA), new IntValue(0));
  }

  @Property
  void distribution(
      @ForAll @IntRange(min = -10000, max = 10000) long a,
      @ForAll @IntRange(min = -10000, max = 10000) long b,
      @ForAll @IntRange(min = -10000, max = 10000) long c) {
    NumericValue numA = new IntValue(a);
    NumericValue numB = new IntValue(b);
    NumericValue numC = new IntValue(c);

    NumericValue leftPart = Arithmetic.mul(numA, Arithmetic.add(numB, numC));
    NumericValue rightPart = Arithmetic.add(Arithmetic.mul(numA, numB), Arithmetic.mul(numA, numC));
    assertEquals(leftPart, rightPart);
  }

  @Property
  void real_addition_is_commutative(@ForAll long a, @ForAll long b) {
    if (Double.isNaN(a) || Double.isInfinite(a) || Double.isNaN(b) || Double.isInfinite(b)) {
      return;
    }
    NumericValue numA = new RealValue(a);
    NumericValue numB = new RealValue(b);
    assertEquals(Arithmetic.add(numA, numB), Arithmetic.add(numB, numA));
  }
}
