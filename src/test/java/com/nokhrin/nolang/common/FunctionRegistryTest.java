package com.nokhrin.nolang.common;

import static org.junit.jupiter.api.Assertions.*;

import com.nokhrin.nolang.common.core.FunctionRegistry;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FunctionRegistryTest {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream testOutput = new PrintStream(outputStream);
    FunctionRegistry functionRegistry = new FunctionRegistry(testOutput);

    static Stream<Arguments> knownBuiltIns() {
        return Stream.of(
            Arguments.of("print"), Arguments.of("sin"), Arguments.of("abs"), Arguments.of("pow"));
    }

    static Stream<Arguments> unknownBuiltIns() {
        return Stream.of(
            Arguments.of("qwerty"), Arguments.of("cos"), Arguments.of("impl"), Arguments.of("sqrt"));
    }

    @ParameterizedTest
    @MethodSource("knownBuiltIns")
    void isBuiltIn_knownFuncs_returnsTrue(String funcName) {
        assertTrue(functionRegistry.isBuiltin(funcName));
    }

    @ParameterizedTest
    @MethodSource("knownBuiltIns")
    void get_knownFuncs_returnsNonNull(String funcName) {
        assertNotNull(functionRegistry.fetch(funcName));
    }

    @ParameterizedTest
    @MethodSource("unknownBuiltIns")
    void isBuiltIn_unknownFuncs_returnsTrue(String funcName) {
        assertFalse(functionRegistry.isBuiltin(funcName));
    }

    @ParameterizedTest
    @MethodSource("unknownBuiltIns")
    void get_unknownFuncs_returnsNull(String funcName) {
        assertTrue(functionRegistry.fetch(funcName).isEmpty());
    }
}
