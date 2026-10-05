package com.nokhrin.nolang;

import com.nokhrin.nolang.algebraic.AlgebraicInterpreter;
import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.core.EvalResult;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.core.FunctionRegistry;
import com.nokhrin.nolang.common.core.Scope;
import com.nokhrin.nolang.common.values.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/** Точка входа для REPL алгебраического калькулятора. */
public class AlgebraicRunner {
    private static ExecutionContext handleResult(
        EvalResult<Value> result, PrintStream stdout, PrintStream error) {
        result.executionContext().stdout().forEach(stdout::println);

        return switch (result) {
            case EvalResult.Returned<Value> returned -> {
                stdout.flush();
                yield returned.executionContext().withOutput(List.of());
            }

            case EvalResult.Interrupted<Value> interrupted -> {
                stdout.flush();
                error.println(interrupted.reason().message());
                error.flush();
                yield interrupted.executionContext().withOutput(List.of());
            }
        };
    }

    static void run(String[] args) {
        InputStream input = System.in;
        PrintStream output = System.out;
        PrintStream error = System.err;
        FunctionRegistry functionRegistry = new FunctionRegistry(BuiltInFunctions.create());
        AlgebraicInterpreter interpreter = AlgebraicInterpreter.create();

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
        PrintStream stdout,
        PrintStream stderr) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            stdout.flush();
            stderr.println("File not found: " + filePath);
            stderr.flush();
            return;
        }

        ExecutionContext executionContext =
            new ExecutionContext(new Scope(), functionRegistry, List.of());

        try {
            String fileContent = Files.readString(path);
            EvalResult<Value> result = interpreter.evaluate(fileContent, executionContext);
            handleResult(result, stdout, stderr);

        } catch (IOException e) {
            stdout.flush();
            stderr.println("IO cause: " + e);
            stderr.flush();
        }
    }

    private static void runInteractive(
        AlgebraicInterpreter interpreter,
        FunctionRegistry functionRegistry,
        InputStream stdin,
        PrintStream stdout,
        PrintStream stderr) {
        Scanner scanner = new Scanner(stdin);
        stdout.println("Algebraic Interpreter\n'/h' for usage info, '/q' to quit");
        stdout.flush();

        ExecutionContext executionContext =
            new ExecutionContext(new Scope(), functionRegistry, List.of());

        label:
        while (true) {
            stdout.print("> ");
            stdout.flush();

            if (!scanner.hasNextLine()) {
                break;
            }

            String inputLine = scanner.nextLine().trim();
            switch (inputLine) {
                case "":
                    continue;
                case "/q":
                    break label;
                case "/h":
                    stdout.println(functionRegistry.getRegistryHelp());
                    stdout.flush();
                    continue;
            }

            if (inputLine.startsWith("/h ")) {
                String[] funcHelpCall = inputLine.split("\\s+", 2);
                stdout.println(functionRegistry.getFuncHelp(funcHelpCall[1].trim()));
                stdout.flush();
                continue;
            }

            ExecutionContext envForOutput = executionContext.withOutput(List.of());
            EvalResult<Value> result = interpreter.evaluate(inputLine, envForOutput);
            executionContext = handleResult(result, stdout, stderr);
        }
    }
}
