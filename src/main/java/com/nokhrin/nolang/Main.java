package com.nokhrin.nolang;

import java.util.Arrays;

public class Main {
    static void main(String[] args) {
        String executionMode = "algebraic";
        String[] runnerArgs = new String[0];
        if (args.length > 0) {
            executionMode = args[0];
            runnerArgs = Arrays.copyOfRange(args, 1, args.length);
        }

        switch (executionMode) {
            case "algebraic", "1" -> AlgebraicRunner.run(runnerArgs);
            case "dynamic", "2" -> DynamicTypedRunner.run(runnerArgs);
            case "static", "3" -> StaticTypedRunner.run(runnerArgs);
            default -> {
                System.err.println("Unknown execution mode: " + executionMode);
                System.exit(1);
            }
        }
    }
}
