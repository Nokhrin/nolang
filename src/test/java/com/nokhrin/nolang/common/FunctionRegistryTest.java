package com.nokhrin.nolang.common;

import static org.testng.Assert.*;

import com.nokhrin.nolang.common.executions.FunctionRegistry;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FunctionRegistryTest {
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream testOutput = new PrintStream(outputStream);
  FunctionRegistry functionRegistry = new FunctionRegistry(testOutput);

  @DataProvider
  private Object[][] knownBuiltIns() {
    return new Object[][] {
      {"print"}, {"sin"}, {"abs"}, {"pow"},
    };
  }

  @DataProvider
  private Object[][] unknownBuiltIns() {
    return new Object[][] {
      {"qwerty"}, {"cos"}, {"impl"}, {"sqrt"},
    };
  }

  @Test(dataProvider = "knownBuiltIns")
  void isBuiltIn_knownFuncs_returnsTrue(String funcName) {
    assertTrue(functionRegistry.isBuiltin(funcName));
  }

  @Test(dataProvider = "knownBuiltIns")
  void get_knownFuncs_returnsNonNull(String funcName) {
    assertNotNull(functionRegistry.fetch(funcName));
  }

  @Test(dataProvider = "unknownBuiltIns")
  void isBuiltIn_unknownFuncs_returnsTrue(String funcName) {
    assertFalse(functionRegistry.isBuiltin(funcName));
  }

  @Test(dataProvider = "unknownBuiltIns")
  void get_unknownFuncs_returnsNull(String funcName) {
    assertTrue(functionRegistry.fetch(funcName).isEmpty());
  }
}
