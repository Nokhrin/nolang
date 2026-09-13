package com.nokhrin.nolang.common.functional;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.definitions.FunctionParameter;
import com.nokhrin.nolang.common.operations.Numeric;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BuiltInFunctions {
    private BuiltInFunctions() {
        throw new UnsupportedOperationException("Utility class");
    }


    static Map<String, Function> create() {
        Map<String, Function> builtins = new LinkedHashMap<>();
        builtins.put("print", print());
        builtins.put("sin", sin());
        builtins.put("abs", abs());
        builtins.put("pow", pow());

        return Map.copyOf(builtins);
    }

    private static Function pow() {
        return new Function.BuiltIn(
            new FunctionSignature(
                "pow",
                List.of(
                    new FunctionParameter("base", new Type.Numeric()),
                    new FunctionParameter("exponent", new Type.Numeric())
                ),
                new Type.Real(),
                Arity.exact(2)
            ),

            (_, args) -> {
                Value baseArg = args.getFirst();
                Value exponentArg = args.get(1);
                return switch (baseArg) {
                    case NumericValue.Int intBase -> switch (exponentArg) {
                        case NumericValue.Int intExp -> Numeric.pow(intBase, intExp).widen();
                        case NumericValue.Real realExp -> Numeric.pow(intBase, realExp).widen();
                        default ->
                            Eval.raiseError(new EvalError.TypeError("pow expected numeric, got base: " + baseArg + ", " +
                                "got exponent: " + exponentArg));
                    };
                    case NumericValue.Real intBase -> switch (exponentArg) {
                        case NumericValue.Int intExp -> Numeric.pow(intBase, intExp).widen();
                        case NumericValue.Real realExp -> Numeric.pow(intBase, realExp).widen();
                        default ->
                            Eval.raiseError(new EvalError.TypeError("pow expected numeric, got base: " + baseArg + ", " +
                                "got exponent: " + exponentArg));
                    };
                    default ->
                        Eval.raiseError(new EvalError.TypeError("pow expected numeric, got base: " + baseArg + ", " +
                            "got exponent: " + exponentArg));
                };
            },

            "pow(base, exponent) - <base> raised to <exponent>\nExample: pow(2, 3) -> 8");
    }

    private static Function abs() {
        return new Function.BuiltIn(
            new FunctionSignature("abs",
                List.of(new FunctionParameter("x", new Type.Numeric())),
                new Type.Real(), Arity.exact(1)),

            (_, args) -> {
                Value value = args.getFirst();
                return switch (value) {
                    case NumericValue.Int anInt -> Numeric.abs(anInt).widen();
                    case NumericValue.Real real -> Numeric.abs(real).widen();
                    case Value.Bool bool -> Eval.raiseError(new EvalError.TypeError("Numeric expected, " +
                        "got: " + bool.value()));
                    case Value.Void aVoid -> Eval.raiseError(new EvalError.TypeError("Numeric expected, " +
                        "got: " + aVoid));
                };
            },

            "abs(x) - absolute number of number <x>\nExample: abs(-1) -> 1");
    }

    private static Function sin() {
        return new Function.BuiltIn(
            new FunctionSignature("sin",
                List.of(new FunctionParameter("x", new Type.Numeric())),
                new Type.Real(), Arity.exact(1)),

            (_, args) -> {
                Value number = args.getFirst();
                return switch (number) {
                    case NumericValue.Int anInt -> Numeric.abs(anInt).widen();
                    case NumericValue.Real real -> Numeric.abs(real).widen();
                    case Value.Bool bool -> Eval.raiseError(new EvalError.TypeError("Numeric expected, " +
                        "got: " + bool.value()));
                    case Value.Void aVoid -> Eval.raiseError(new EvalError.TypeError("Numeric expected, " +
                        "got: " + aVoid));
                };
            },

            "sin(x) - sine of angle <x> in radians\nExample: sin(0) -> 0.0");
    }

    private static Function print() {
        return new Function.BuiltIn(
            new FunctionSignature("print", List.of(), new Type.Void(), Arity.atLeast(0)),

            (_, args) ->
                EnvironmentCombinators.modifyEnvironment(environment -> {
                    List<String> outputBuffer = new ArrayList<>(environment.outputBuffer());
                    for (Value value : args) {
                        outputBuffer.add(value.toString());
                    }
                    return environment.withOutput(outputBuffer);
                }).map(_ -> Value.Void.INSTANCE),

            "print(number) - output <number> to stdout, accepts 0 or more parameters\nExample: print(42)");
    }

}
