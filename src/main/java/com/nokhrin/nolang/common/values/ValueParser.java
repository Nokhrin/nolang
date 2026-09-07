package com.nokhrin.nolang.common.values;

import com.nokhrin.nolang.functional.Eval;
import com.nokhrin.nolang.functional.EvalError;

public class ValueParser {

    public static Eval<NumericValue> parseNumber(String lexeme) {
        try {

            if (lexeme.contains(".") || lexeme.contains("e") || lexeme.contains("E")) {
                return Eval.pure(new RealValue(Double.parseDouble(lexeme)));
            }
            return Eval.pure(new IntValue(Long.parseLong(lexeme)));
        } catch (NumberFormatException e) {
            return Eval.raiseError(new EvalError.SyntaxError("Invalid numeric literal: " + lexeme));
        }

    }

    public static Eval<BoolValue> parseBoolean(String lexeme) {
        return switch (lexeme) {
            case "true" -> Eval.pure(new BoolValue(true));
            case "false" -> Eval.pure(new BoolValue(false));
            default -> throw new IllegalArgumentException("Not a boolean value: " + lexeme);
        };
    }
}
