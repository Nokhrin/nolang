package com.nokhrin.nolang;

import com.nokhrin.nolang.algebraic.AlgebraicInterpreter;
import com.nokhrin.nolang.common.core.Environment;
import com.nokhrin.nolang.common.core.FunctionRegistry;
import com.nokhrin.nolang.common.core.Result;
import com.nokhrin.nolang.common.core.Scope;
import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.values.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Точка входа для REPL алгебраического калькулятора.
 */
public class AlgebraicRunner {
    static void run(String[] args) {
        InputStream input = System.in;
        PrintStream output = System.out;
        PrintStream error = System.err;
        FunctionRegistry functionRegistry = new FunctionRegistry(BuiltInFunctions.create());
        AlgebraicInterpreter interpreter = AlgebraicInterpreter.monadic();

        if (args.length == 1) {
            executeFile(interpreter, args[0], functionRegistry, output, error);
            return;
        }

        runInteractive(interpreter, functionRegistry, input, output, error);
    }

    private static void executeFile(
        AlgebraicInterpreter interpreter,
        String filePath,
        FunctionRegistry functionRegistry,
        PrintStream output,
        PrintStream error) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            error.println("File not found: " + filePath);
            error.flush();
            return;
        }

        Environment environment = new Environment(new Scope(), functionRegistry, List.of());

        try {
            String fileContent = Files.readString(path);

            Result<Value> result = interpreter.interpret(fileContent, environment);
            result.environment().outputBuffer().forEach(output::println);

            switch (result) {
                case Result.Success<Value> success -> environment = success.environment().withOutput(List.of());
                case Result.Failure<Value> failure -> {
                    environment = failure.environment().withOutput(List.of());
                    error.println(failure.error().message());
                    error.flush();
                }
                case Result.Control<Value> control -> environment = control.environment().withOutput(List.of());
            }

        } catch (IOException e) {
            error.println("IO error: " + e);
            error.flush();
        }
        output.flush();
    }

    private static void runInteractive(
        AlgebraicInterpreter interpreter,
        FunctionRegistry functionRegistry,
        InputStream input,
        PrintStream output,
        PrintStream error) {
        Scanner scanner = new Scanner(input);
        output.println("Algebraic Interpreter\n'/h' for usage info, '/q' to quit");
        output.flush();

        Environment environment = new Environment(new Scope(), functionRegistry, List.of());

        label:
        while (scanner.hasNextLine()) {
            output.print("> ");
            output.flush();

            String inputLine = scanner.nextLine().trim();
            switch (inputLine) {
                case "":
                    continue;
                case "/q":
                    break label;
                case "/h":
                    output.println(functionRegistry.getRegistryHelp());
                    output.flush();
                    continue;
            }

            if (inputLine.startsWith("/h ")) {
                String[] funcHelpCall = inputLine.split("\\s+", 2);
                output.println(functionRegistry.getFuncHelp(funcHelpCall[1].trim()));
                output.flush();
                continue;
            }

            Environment envForOutput = environment.withOutput(List.of());
            Result<Value> result = interpreter.interpret(inputLine, envForOutput);
            result.environment().outputBuffer().forEach(output::println);

            switch (result) {
                case Result.Success<Value> success -> environment = success.environment().withOutput(List.of());
                case Result.Failure<Value> failure -> {
                    environment = envForOutput.withOutput(List.of());
                    error.println(failure.error().message());
                    error.flush();
                }
                case Result.Control<Value> control -> environment = envForOutput.withOutput(List.of());
            }
        }
    }
}
