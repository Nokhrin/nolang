package com.nokhrin.nolang.common.combinators;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;

public class ValueParser {

    public static Eval<NumericValue> parseNumber(String lexeme) {
        try {

            if (lexeme.contains(".") || lexeme.contains("e") || lexeme.contains("E")) {
                return Eval.pure(new NumericValue.RealValue(Double.parseDouble(lexeme)));
            }
            return Eval.pure(new NumericValue.IntValue(Long.parseLong(lexeme)));
        } catch (NumberFormatException e) {
            return Eval.raiseError(new EvalError.SyntaxError("Invalid numeric literal: " + lexeme));
        }
    }

    public static Eval<Value.BoolValue> parseBoolean(String lexeme) {
        return switch (lexeme) {
            case "true" -> Eval.pure(new Value.BoolValue(true));
            case "false" -> Eval.pure(new Value.BoolValue(false));
            default -> Eval.raiseError(new EvalError.SyntaxError("Not a boolean value: " + lexeme));
        };
    }
}
