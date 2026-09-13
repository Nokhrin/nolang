package com.nokhrin.nolang.common.functional;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

public class ValueParser {

    public static Eval<NumericValue> parseNumber(String lexeme) {
        try {

            if (lexeme.contains(".") || lexeme.contains("e") || lexeme.contains("E")) {
                return Eval.pure(new NumericValue.Real(Double.parseDouble(lexeme)));
            }
            return Eval.pure(new NumericValue.Int(Long.parseLong(lexeme)));
        } catch (NumberFormatException e) {
            return Eval.raiseError(new EvalError.SyntaxError("Invalid numeric literal: " + lexeme));
        }

    }

    public static Eval<Value.Bool> parseBoolean(String lexeme) {
        return switch (lexeme) {
            case "true" -> Eval.pure(new Value.Bool(true));
            case "false" -> Eval.pure(new Value.Bool(false));
            default -> Eval.raiseError(new EvalError.SyntaxError("Not a boolean value: " + lexeme));
        };
    }
}
