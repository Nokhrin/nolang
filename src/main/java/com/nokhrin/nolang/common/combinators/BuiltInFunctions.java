package com.nokhrin.nolang.common.combinators;

import com.nokhrin.nolang.common.Type;
import com.nokhrin.nolang.common.core.*;
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

  public static Map<String, Function> create() {
    Map<String, Function> builtins = new LinkedHashMap<>();
    builtins.put("print", print());
    builtins.put("sin", sin());
    builtins.put("abs", abs());
    builtins.put("pow", pow());

    return Map.copyOf(builtins);
  }

  private static Function pow() {
    return new Function(
        new FunctionSignature(
            "pow",
            List.of(
                new Declaration.Parameter("base", new Type.NumericType()),
                new Declaration.Parameter("exponent", new Type.NumericType())),
            new Type.NumericType(),
            Arity.exact(2)),
        (_, args) -> {
          Value baseArg = args.getFirst();
          Value exponentArg = args.get(1);
          return switch (baseArg) {
            case NumericValue.IntValue intValueBase ->
                switch (exponentArg) {
                  case NumericValue.IntValue intValueExp ->
                      EvalCombinators.upcastToValue(Numeric.pow(intValueBase, intValueExp));
                  case NumericValue.RealValue realValueExp ->
                      EvalCombinators.upcastToValue(Numeric.pow(intValueBase, realValueExp));
                  default ->
                      Eval.raiseError(
                          new EvalError.TypeError(
                              "pow expected numeric, got base: %s, got exponent: %s"
                                  .formatted(baseArg, exponentArg)));
                };
            case NumericValue.RealValue realBase ->
                switch (exponentArg) {
                  case NumericValue.IntValue intValueExp ->
                      EvalCombinators.upcastToValue(Numeric.pow(realBase, intValueExp));
                  case NumericValue.RealValue realValueExp ->
                      EvalCombinators.upcastToValue(Numeric.pow(realBase, realValueExp));
                  default ->
                      Eval.raiseError(
                          new EvalError.TypeError(
                              "pow expected numeric, got base: %s, got exponent: %s"
                                  .formatted(baseArg, exponentArg)));
                };
            default ->
                Eval.raiseError(
                    new EvalError.TypeError(
                        "pow expected numeric, got base: %s, got exponent: %s"
                            .formatted(baseArg, exponentArg)));
          };
        },
        "pow(base, exponent) - <base> raised to <exponent>\nExample: pow(2, 3) -> 8");
  }

  private static Function abs() {
    return new Function(
        new FunctionSignature(
            "abs",
            List.of(new Declaration.Parameter("x", new Type.NumericType())),
            new Type.NumericType(),
            Arity.exact(1)),
        (_, args) -> {
          Value value = args.getFirst();
          return switch (value) {
            case NumericValue.IntValue anIntValue ->
                EvalCombinators.upcastToValue(Numeric.abs(anIntValue));
            case NumericValue.RealValue realValue ->
                EvalCombinators.upcastToValue(Numeric.abs(realValue));
            case Value.BoolValue boolValue ->
                Eval.raiseError(
                    new EvalError.TypeError("Numeric expected, " + "got: " + boolValue.value()));
            case Value.VoidValue aVoidValue ->
                Eval.raiseError(
                    new EvalError.TypeError("Numeric expected, " + "got: " + aVoidValue));
          };
        },
        "abs(x) - absolute number of number <x>\nExample: abs(-1) -> 1");
  }

  private static Function sin() {
    return new Function(
        new FunctionSignature(
            "sin",
            List.of(new Declaration.Parameter("x", new Type.NumericType())),
            new Type.RealType(),
            Arity.exact(1)),
        (_, args) -> {
          Value number = args.getFirst();
          return switch (number) {
            case NumericValue.IntValue anIntValue ->
                EvalCombinators.upcastToValue(Numeric.sin(anIntValue));
            case NumericValue.RealValue realValue ->
                EvalCombinators.upcastToValue(Numeric.sin(realValue));
            case Value.BoolValue boolValue ->
                Eval.raiseError(
                    new EvalError.TypeError("Numeric expected, " + "got: " + boolValue.value()));
            case Value.VoidValue aVoidValue ->
                Eval.raiseError(
                    new EvalError.TypeError("Numeric expected, " + "got: " + aVoidValue));
          };
        },
        "sin(x) - sine of angle <x> in radians\nExample: sin(0) -> 0.0");
  }

  private static Function print() {
    return new Function(
        new FunctionSignature("print", List.of(), new Type.VoidType(), Arity.atLeast(0)),
        (_, args) ->
            ContextCombinators.updateContext(
                    environment -> {
                      List<String> outputBuffer = new ArrayList<>(environment.stdout());
                      for (Value value : args) {
                        outputBuffer.add(value.toString());
                      }
                      return environment.withOutput(outputBuffer);
                    })
                .map(_ -> Value.VoidValue.INSTANCE),
        "print(number) - output <number> to stdout, accepts 0 or more parameters\nExample: print(42)");
  }
}
