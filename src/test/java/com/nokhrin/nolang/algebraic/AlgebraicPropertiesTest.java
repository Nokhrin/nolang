package com.nokhrin.nolang.algebraic;

import static org.testng.Assert.assertEquals;

import java.io.PrintStream;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.testng.annotations.BeforeMethod;

public class AlgebraicPropertiesTest {
  AlgebraicEvaluator evaluator = null;

  @BeforeMethod
  private void setUp() {
    evaluator = new AlgebraicEvaluator(new PrintStream(System.out));
  }

  @Property
  void additionIsCommutative(@ForAll int a, @ForAll int b) {
    assertEquals(evaluator.evaluate(a + "+" + b), evaluator.evaluate(b + "+" + a));
  }
}
