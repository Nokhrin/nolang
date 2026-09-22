// package com.nokhrin.nolang.common;
//
// import com.nokhrin.nolang.common.operations.Logical;
// import com.nokhrin.nolang.common.values.Value;
// import net.jqwik.api.ForAll;
// import net.jqwik.api.Property;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// public class LogicalPropertiesTest {
//    /**
//     * and(a, a) == a
//     */
//    @Property
//    void idempotence_and(@ForAll boolean a) {
//        Value.Bool aBool = new Value.Bool(a);
//        assertEquals(aBool, Logical.and(aBool, aBool));
//    }
//
//    /**
//     * or(a, a) == a
//     */
//    @Property
//    void idempotence_or(@ForAll boolean a) {
//        assertEquals(new Value.Bool(a), Logical.or(new Value.Bool(a), new Value.Bool(a)));
//    }
//
//    /**
//     * not(and(a, b)) == or(not(a), not(b))
//     */
//    @Property
//    void deMorgan_law_and(@ForAll boolean a, @ForAll boolean b) {
//        assertEquals(
//            Logical.not(Logical.and(new Value.Bool(a), new Value.Bool(b))),
//            Logical.or(Logical.not(new Value.Bool(a)), Logical.not(new Value.Bool(b)))
//        );
//    }
//
//    @Property
//    void deMorgan_law_or(@ForAll boolean a, @ForAll boolean b) {
//        Value aBool = new Value.Bool(a);
//        Value bBool = new Value.Bool(b);
//        assertEquals(
//            Logical.not(Logical.or(aBool, bBool)), Logical.and(Logical.not(aBool),
// Logical.not(bBool)));
//    }
//
//    /**
//     * not(not(a)) == a
//     */
//    @Property
//    void double_neg(@ForAll boolean a) {
//        Value aBool = new Value.Bool(a);
//        assertEquals(aBool, Logical.not(Logical.not(aBool)));
//    }
//
//    /**
//     * and(a, b) == and(b, a)
//     */
//    @Property
//    void commutativity(@ForAll boolean a, @ForAll boolean b) {
//        assertEquals(Logical.and(new Value.Bool(a), new Value.Bool(b)),
//            Logical.and(new Value.Bool(b), new Value.Bool(b)));
//    }
//
//    /**
//     * and(a, or(a, b)) == a
//     */
//    @Property
//    void absorption(@ForAll boolean a, @ForAll boolean b) {
//        assertEquals(new Value.Bool(a), Logical.and(new Value.Bool(a),
//            Logical.or(new Value.Bool(a), new Value.Bool(b))));
//    }
// }
