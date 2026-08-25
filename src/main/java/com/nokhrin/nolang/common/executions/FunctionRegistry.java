package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.errors.SemanticException;
import com.nokhrin.nolang.common.operations.Arithmetic;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.RealValue;
import com.nokhrin.nolang.common.values.Value;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FunctionRegistry {
    private final Map<String, Function> functions = new HashMap<>();
    private final PrintStream output;

    public FunctionRegistry(PrintStream stream) {
        output = stream;
        registerBuiltins();
    }

    private void register(BuiltinFunction function) {
        if (functions.containsKey(function.name())) {
            throw new IllegalStateException("Function already registred: " + function.name());
        }
        functions.put(function.name(), function);
    }

    private void registerBuiltins() {
        register(
            new BuiltinFunction(
                new FunctionSignature("print", List.of()),
                args -> {
                    for (Value value : args) {
                        output.println(value.toString());
                        output.flush();
                    }
                    return new VoidResult();
                },
                "print(number) - output <number> to stdout, accepts 0 or more parameters\nExample: print(42)"));

        register(
            new BuiltinFunction(
                new FunctionSignature("sin", List.of(new ParameterSymbol("x", Type.REAL))),
                args -> {
                    NumericValue value = args.getFirst().asNumeric();
                    double doubleValue =
                        switch (value) {
                            case IntValue v -> (double) v.number();
                            case RealValue v -> v.number();
                        };
                    return new ValueResult(new RealValue(Math.sin(doubleValue)));
                },
                "sin(x) - sine of angle <x> in radians\nExample: sin(0) -> 0.0"));

        register(
            new BuiltinFunction(
                new FunctionSignature("abs", List.of(new ParameterSymbol("x", Type.REAL))),
                args -> {
                    NumericValue number = args.getFirst().asNumeric();
                    return new ValueResult(Arithmetic.abs(number));
                },
                "abs(x) - absolute number of number <x>\nExample: abs(-1) -> 1"));

        register(
            new BuiltinFunction(
                new FunctionSignature(
                    "pow",
                    List.of(
                        new ParameterSymbol("base", Type.REAL),
                        new ParameterSymbol("exponent", Type.REAL))),
                args ->
                    new ValueResult(Arithmetic.pow(args.get(0).asNumeric(), args.get(1).asNumeric())),
                "pow(base, exponent) - <base> raised to <exponent>\nExample: pow(2, 3) -> 8"));
    }

    public boolean isBuiltin(String funcName) {
        return functions.containsKey(funcName);
    }

    public void define(Function function) {
        functions.put(function.name(), function);
    }

    public Optional<Function> fetch(String funcName) {
        return Optional.ofNullable(functions.get(funcName));
    }

    public String getFuncHelp(String funcName) {
        return fetch(funcName)
            .map(
                function ->
                    switch (function) {
                        case BuiltinFunction builtinFunction -> builtinFunction.helpText();
                        case UserFunction userFunction ->
                            "No help available for user-defined function: " + userFunction.name();
                    })
            .orElse("Not registered function: " + funcName);
    }

    public String getGeneralHelp() {
        String registeredFunctions =
            functions.keySet().stream().map(k -> " " + k).reduce("", (a, b) -> a + "\n" + b);
        String helpText =
            "Built-in functions:\n"
                + registeredFunctions
                + "\n"
                + "Use /h <function name> for details. Example: /h sin";
        return helpText;
    }

    public Result invokeFunction(String funcName, List<Value> args) {
        Function function = this.fetch(funcName)
            .orElseThrow(() -> new SemanticException("Undefined function: " + funcName));
        return function.invoke(args);
    }
}
