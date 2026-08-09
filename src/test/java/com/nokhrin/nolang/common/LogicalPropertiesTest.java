package com.nokhrin.nolang.common;

import com.nokhrin.nolang.common.operations.Logical;
import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.common.values.Value;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.testng.Assert.assertEquals;

public class LogicalPropertiesTest {
    /**
     * and(a, a) == a
     */
    @Property
    void idempotence_and(@ForAll boolean a) {
        Value aBool = new BoolValue(a);
        assertEquals(aBool, Logical.and(aBool, aBool));
    }

    /**
     * or(a, a) == a
     */
    @Property
    void idempotence_or(@ForAll boolean a) {
        Value aBool = new BoolValue(a);
        assertEquals(aBool, Logical.or(aBool, aBool));
    }

    /**
     * not(and(a, b)) == or(not(a), not(b))
     */
    @Property
    void deMorgan_law(
            @ForAll boolean a,
            @ForAll boolean b
    ) {
        Value aBool = new BoolValue(a);
        Value bBool = new BoolValue(b);
        assertEquals(
                Logical.not(Logical.and(aBool, bBool)),
                Logical.or(Logical.not(aBool), Logical.not(bBool))
        );
    }

    /**
     * not(not(a)) == a
     */
    @Property
    void double_neg(@ForAll boolean a) {
        Value aBool = new BoolValue(a);
        assertEquals(aBool,
                Logical.not(Logical.not(aBool))
        );
    }

    /**
     * and(a, b) == and(b, a)
     */
    @Property
    void commutativity(
            @ForAll boolean a,
            @ForAll boolean b
    ) {
        Value aBool = new BoolValue(a);
        Value bBool = new BoolValue(b);
        assertEquals(
                Logical.and(aBool, bBool),
                Logical.and(bBool, aBool)
        );
    }

    /**
     * and(a, or(a, b)) == a
     */
    @Property
    void absorption(
            @ForAll boolean a,
            @ForAll boolean b
    ) {
        Value aBool = new BoolValue(a);
        Value bBool = new BoolValue(b);
        assertEquals(
                aBool,
                Logical.and(
                        aBool,
                        Logical.or(aBool, bBool)
                ));
    }

}