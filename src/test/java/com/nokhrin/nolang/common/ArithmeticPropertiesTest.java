package com.nokhrin.nolang.common;

import static org.testng.Assert.assertEquals;

import com.nokhrin.nolang.common.operations.Arithmetic;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.Value;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

public class ArithmeticPropertiesTest {

  /** add(a, b) == add(b, a) */
  @Property
  void arithmetic_commutative(
      @ForAll @IntRange(min = Integer.MIN_VALUE) long a,
      @ForAll @IntRange(min = Integer.MIN_VALUE) long b) {
    Value aInt = new IntValue(a);
    Value bInt = new IntValue(b);
    assertEquals(Arithmetic.add(aInt, bInt), Arithmetic.add(bInt, aInt));
  }

  /** add(a, 0) == a */
  @Property
  void arithmetic_identity(@ForAll @IntRange(min = Integer.MIN_VALUE) long a) {
    Value aInt = new IntValue(a);
    assertEquals(Arithmetic.add(aInt, new IntValue(0)), aInt);
  }

  /** sub(a, a) == 0 */
  @Property
  void arithmetic_inverse(@ForAll @IntRange(min = Integer.MIN_VALUE) long a) {
    Value aInt = new IntValue(a);
    assertEquals(Arithmetic.sub(aInt, aInt), new IntValue(0));
  }

  /** add(add(a, b), c) == add(a, add(b, c)) Только для `IntValue` */
  @Property
  void arithmetic_associative(
      @ForAll @IntRange(min = Integer.MIN_VALUE) long a,
      @ForAll @IntRange(min = Integer.MIN_VALUE) long b,
      @ForAll @IntRange(min = Integer.MIN_VALUE) long c) {
    Value aInt = new IntValue(a);
    Value bInt = new IntValue(b);
    Value cInt = new IntValue(c);
    assertEquals(
        Arithmetic.add(Arithmetic.add(aInt, bInt), cInt),
        Arithmetic.add(aInt, Arithmetic.add(bInt, cInt)));
  }

  /** mul(a, add(b, c)) == add(mul(a, b), mul(a, c)) */
  @Property
  void arithmetic_distributive(
      @ForAll @IntRange(min = Integer.MIN_VALUE) long a,
      @ForAll @IntRange(min = Integer.MIN_VALUE) long b,
      @ForAll @IntRange(min = Integer.MIN_VALUE) long c) {
    Value aInt = new IntValue(a);
    Value bInt = new IntValue(b);
    Value cInt = new IntValue(c);
    assertEquals(
        Arithmetic.mul(aInt, Arithmetic.add(bInt, cInt)),
        Arithmetic.add(Arithmetic.mul(aInt, bInt), Arithmetic.mul(aInt, cInt)));
  }
}
