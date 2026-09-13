package com.nokhrin.nolang.common;

import com.nokhrin.nolang.common.operations.Numeric;
import com.nokhrin.nolang.common.values.NumericValue;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumericPropertiesTest {
    @Property
    void addition_is_commutative(@ForAll long a, @ForAll long b) {
        NumericValue numA = new NumericValue.Int(a);
        NumericValue numB = new NumericValue.Int(b);
        assertEquals(Numeric.add(numA, numB), Numeric.add(numB, numA));
    }

    @Property
    void multiplication_is_commutative(@ForAll long a, @ForAll long b) {
        NumericValue numA = new NumericValue.Int(a);
        NumericValue numB = new NumericValue.Int(b);
        assertEquals(Numeric.mul(numA, numB), Numeric.mul(numB, numA));
    }

    @Property
    void addition_identity(@ForAll long a) {
        NumericValue numA = new NumericValue.Int(a);
        assertEquals(Numeric.add(numA, new NumericValue.Int(0)), numA);
    }

    @Property
    void multiplication_identity(@ForAll long a) {
        NumericValue numA = new NumericValue.Int(a);
        assertEquals(Numeric.mul(numA, new NumericValue.Int(1)), numA);
    }

    @Property
    void subtraction_inverse(@ForAll long a) {
        NumericValue numA = new NumericValue.Int(a);
        assertEquals(Numeric.sub(numA, numA), new NumericValue.Int(0));
    }

    @Property
    void distribution(
        @ForAll @IntRange(min = -10000, max = 10000) long a,
        @ForAll @IntRange(min = -10000, max = 10000) long b,
        @ForAll @IntRange(min = -10000, max = 10000) long c) {
        NumericValue numA = new NumericValue.Int(a);
        NumericValue numB = new NumericValue.Int(b);
        NumericValue numC = new NumericValue.Int(c);

        NumericValue leftPart = Numeric.mul(numA, Numeric.add(numB, numC));
        NumericValue rightPart = Numeric.add(Numeric.mul(numA, numB), Numeric.mul(numA, numC));
        assertEquals(leftPart, rightPart);
    }

    @Property
    void real_addition_is_commutative(@ForAll long a, @ForAll long b) {
        if (Double.isNaN(a) || Double.isInfinite(a) || Double.isNaN(b) || Double.isInfinite(b)) {
            return;
        }
        NumericValue numA = new NumericValue.Real(a);
        NumericValue numB = new NumericValue.Real(b);
        assertEquals(Numeric.add(numA, numB), Numeric.add(numB, numA));
    }
}
