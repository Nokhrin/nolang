package com.nokhrin.nolang.common.executions;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.definitions.ParameterSymbol;
import com.nokhrin.nolang.common.values.*;
import com.nokhrin.nolang.functional.Eval;
import com.nokhrin.nolang.functional.EvalError;

import java.util.*;

public record FunctionRegistry(Map<String, Function> functions) {

    public FunctionRegistry() {
        this(createBuiltins());
    }

    private static Map<String, Function> createBuiltins() {
        Map<String, Function> builtins = new HashMap<>();
        builtins.put(
            "print",
            new BuiltinFunction(
                new FunctionSignature("print", List.of(), new Type.VoidType()),
                args -> Eval.modifyEnvironment(environment -> {
                    List<String> outputBuffer = new ArrayList<>(environment.outputBuffer());
                    for (Value value : args) {
                        outputBuffer.add(value.toString());
                    }
                    return environment.withOutput(outputBuffer);
                }).map(_ -> VoidValue.INSTANCE),
                "print(number) - output <number> to stdout, accepts 0 or more parameters\nExample: print(42)"));

        builtins.put(
            "sin",
            new BuiltinFunction(
                new FunctionSignature("sin", List.of(new ParameterSymbol("x", new Type.RealType())), new Type.RealType()),
                args -> {
                    Value value = args.getFirst();
                    if (value instanceof NumericValue num) {
                        double doubleNum = switch (num) {
                            case IntValue v -> (double) v.number();
                            case RealValue v -> v.number();
                        };
                        return Eval.pure(new RealValue(Math.sin(doubleNum)));
                    }
                    return Eval.raiseError(new EvalError.TypeError("sin expected numeric, got: " + value));
                },
                "sin(x) - sine of angle <x> in radians\nExample: sin(0) -> 0.0"));

        builtins.put(
            "abs",
            new BuiltinFunction(
                new FunctionSignature("abs", List.of(new ParameterSymbol("x", new Type.RealType())), new Type.RealType()),
                args -> {
                    Value value = args.getFirst();
                    if (value instanceof NumericValue num) {
                        double doubleNum = switch (num) {
                            case IntValue v -> (double) v.number();
                            case RealValue v -> v.number();
                        };
                        return Eval.pure(new RealValue(Math.abs(doubleNum)));
                    }
                    return Eval.raiseError(new EvalError.TypeError("abs expected numeric, got: " + value));
                },
                "abs(x) - absolute number of number <x>\nExample: abs(-1) -> 1"));

        builtins.put(
            "pow",
            new BuiltinFunction(
                new FunctionSignature(
                    "pow",
                    List.of(
                        new ParameterSymbol("base", new Type.RealType()),
                        new ParameterSymbol("exponent", new Type.RealType())
                    ),
                    new Type.RealType()
                ),
                args -> {
                    Value baseArg = args.getFirst();
                    Value exponentArg = args.get(1);
                    if (baseArg instanceof NumericValue base
                        && exponentArg instanceof NumericValue exponent) {

                        double doubleBase = switch (base) {
                            case IntValue v -> (double) v.number();
                            case RealValue v -> v.number();
                        };
                        double doubleExponent = switch (exponent) {
                            case IntValue v -> (double) v.number();
                            case RealValue v -> v.number();
                        };
                        return Eval.pure(new RealValue(Math.pow(doubleBase, doubleExponent)));
                    }
                    return Eval.raiseError(new EvalError.TypeError("pow expected numeric, got base: " + baseArg + ", " +
                        "got exponent: " + exponentArg));
                },
                "pow(base, exponent) - <base> raised to <exponent>\nExample: pow(2, 3) -> 8"));
        return builtins;
    }

    public boolean isBuiltin(String funcName) {
        return functions.containsKey(funcName);
    }

    public Optional<Function> fetch(String funcName) {
        return Optional.ofNullable(functions.get(funcName));
    }

    public Eval<Value> invoke(String funcName, List<Value> args) {
        Function function =
            this.fetch(funcName)
                .orElseThrow(() -> new IllegalStateException("Undefined function: " + funcName));
        return function.invoke(args);
    }

    public FunctionRegistry define(FunctionSignature signature, FunctionBody body, Scope scope) {
        Map<String, Function> childFunctions = new HashMap<>(functions);
        childFunctions.put(signature.name(), new UserFunction(signature, body, scope));
        return new FunctionRegistry(Map.copyOf(childFunctions));
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
        String registeredFunctions = String.join("\n", functions.keySet());
        return "Built-in functions:\n"
            + registeredFunctions
            + "\n"
            + "Use /h <function name> for details. Example: /h sin";
    }
}
