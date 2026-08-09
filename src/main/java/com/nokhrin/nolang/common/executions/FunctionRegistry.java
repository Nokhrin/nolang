package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.operations.Arithmetic;
import com.nokhrin.nolang.common.values.DoubleValue;
import com.nokhrin.nolang.common.values.Value;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.nokhrin.nolang.common.operations.Arithmetic.toDouble;

public final class FunctionRegistry {
    private final Map<String, Function> functions = new HashMap<>();
    private final PrintStream output;

    public FunctionRegistry(PrintStream stream) {
        output = stream;
        registerBuiltins();
    }


    private void register(BuiltinFunction function) {
        functions.put(function.name(), function);
    }

    private void registerBuiltins() {
        register(new BuiltinFunction(
                new FunctionSignature("print", List.of()),
                args -> {
                    for (Value value : args) {
                        output.println(value.toString());
                    }
                    return new VoidResult();
                },
                "print(number) - output <number> to stdout, accepts 0 or more parameters\nExample: print(42)"
        ));

        register(new BuiltinFunction(
                new FunctionSignature("sin", List.of(new ParameterSymbol("x", Optional.of(Type.FLOAT)))),
                args -> new ValueResult(
                        new DoubleValue(Math.sin(toDouble(args.getFirst())))
                ),
                "sin(x) - sine of angle <x> in radians\nExample: sin(0) -> 0.0"
        ));

        register(new BuiltinFunction(
                new FunctionSignature("abs",List.of(new ParameterSymbol("x", Optional.of(Type.FLOAT)))),
                args -> new ValueResult(
                        Arithmetic.abs(args.getFirst())
                ),
                "abs(x) - absolute number of number <x>\nExample: abs(-1) -> 1"
        ));

        register(new BuiltinFunction(
                new FunctionSignature("pow",List.of(
                        new ParameterSymbol("base", Optional.of(Type.FLOAT)),
                        new ParameterSymbol("exponent", Optional.of(Type.FLOAT))
                )),
                args -> new ValueResult(
                        Arithmetic.pow(args.get(0), args.get(1))
                ),
                "pow(base, exponent) - <base> raised to <exponent>\nExample: pow(2, 3) -> 8"
        ));
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
        Optional<Function> function = fetch(funcName);

        return switch (function.get()) {
            case BuiltinFunction builtinFunction -> builtinFunction.helpText();
            case UserFunction userFunction -> "No help available for user-defined function: " + userFunction.name();
        };
    }

    public String getGeneralHelp() {
        String registeredFunctions = functions
                .keySet()
                .stream()
                .map(k -> " " + k)
                .reduce("", (a, b) -> a + "\n" + b);
        String helpText = "Built-in functions:\n"
                + registeredFunctions + "\n"
                + "Use ?<function name> for details. Example: ?print";
        return helpText;
    }

}
