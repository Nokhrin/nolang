package com.nokhrin.nolang.algebraic;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.testng.annotations.BeforeMethod;

import java.io.PrintStream;

import static org.testng.Assert.assertEquals;

public class AlgebraicPropertiesTest {
    AlgebraicInterpreter evaluator = null;

    @BeforeMethod
    private void setUp() {
        evaluator = new AlgebraicInterpreter(new PrintStream(System.out));
    }

    @Property
    void additionIsCommutative(@ForAll int a, @ForAll int b) {
        assertEquals(evaluator.evaluate(a + "+" + b), evaluator.evaluate(b + "+" + a));
    }
}
