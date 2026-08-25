package com.nokhrin.nolang.common.values;

public class ValueParser {
    public static NumericValue parseNumber(String lexeme) {
        if (lexeme.contains(".")
            || lexeme.contains("e")
            || lexeme.contains("E")
        ) {
            return new RealValue(Double.parseDouble(lexeme));
        }
        return new IntValue(Long.parseLong(lexeme));
    }

    public static BoolValue parseBoolean(String lexeme) {
        return switch (lexeme) {
            case "true" -> new BoolValue(true);
            case "false" -> new BoolValue(false);
            default -> throw new IllegalArgumentException("Not a boolean value: " + lexeme);
        };
    }
}
