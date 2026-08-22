package com.nokhrin.nolang;

import com.nokhrin.nolang.algebraic.AlgebraicInterpreter;
import com.nokhrin.nolang.common.errors.SyntaxException;
import com.nokhrin.nolang.common.executions.*;
import com.nokhrin.nolang.common.values.Value;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Точка входа для REPL алгебраического калькулятора.
 *
 * <p>Примеры использования:
 *
 * {@snippet lang = shell:
 *   # Сборка проекта
 *   mvn clean package
 *
 *   # Интерактивный режим
 *   java -jar target/nolang-1.0-SNAPSHOT.jar
 *
 *   # Пакетный режим
 *   java -jar target/nolang-1.0-SNAPSHOT.jar src/test/resources/algebraic/algebra.txt
 * }
 */
public class AlgebraicRunner {
  static void run(String[] args) {
    InputStream inputStream = System.in;
    PrintStream outputStream = System.out;
    PrintStream errorStream = System.err;
    FunctionRegistry functionRegistry = new FunctionRegistry(outputStream);
    AlgebraicInterpreter interpreter = new AlgebraicInterpreter(outputStream);

    if (args.length == 1) {
      executeFile(interpreter, args[0], functionRegistry, inputStream, outputStream, errorStream);
      return;
    }

    runInteractive(interpreter, functionRegistry, inputStream, outputStream, errorStream);
  }

  private static void executeFile(
      AlgebraicInterpreter interpreter,
      String filePath,
      FunctionRegistry functionRegistry,
      InputStream input,
      PrintStream output,
      PrintStream errors) {
    Path path = Path.of(filePath);
    if (!Files.exists(path)) {
      errors.println("File not found: " + filePath);
      errors.flush();
      return;
    }

    try {
      String fileContent = Files.readString(path);
      Result result = interpreter.evaluate(fileContent);
      switch (result) {
        case ValueResult(Value value) -> output.println(value);
        case VoidResult() -> {}
        case ControlSignal signal ->
            throw new IllegalStateException("Signals are not supported. Provided: " + signal);
      }
    } catch (SyntaxException e) {
      errors.println("Syntax error: " + e);
      errors.flush();
    } catch (IllegalStateException e) {
      errors.println("Runtime error: " + e);
      errors.flush();
    } catch (ArithmeticException e) {
      errors.println("Arithmetic error: " + e);
      errors.flush();
    } catch (Exception e) {
      errors.println("Error: " + e);
      errors.flush();
    }
    output.flush();
  }

  private static void runInteractive(
      AlgebraicInterpreter interpreter,
      FunctionRegistry functionRegistry,
      InputStream input,
      PrintStream output,
      PrintStream errors) {
    Scanner scanner = new Scanner(input);
    output.println("Algebraic Interpreter\n'/h' for usage info, '/q' to quit");

    while (true) {
      output.print("> ");
      if (!scanner.hasNextLine()) {
        break;
      }

      String inputLine = scanner.nextLine().trim();
      if (inputLine.isEmpty()) {
        continue;
      }

      if (inputLine.equals("/q")) {
        break;
      }

      if (inputLine.equals("/h")) {
        output.println(functionRegistry.getGeneralHelp());
        output.flush();
        continue;
      } else if (inputLine.startsWith("/h ")) {
        String[] funcHelpCall = inputLine.split("\\s+", 2);
        output.println(functionRegistry.getFuncHelp(funcHelpCall[1].trim()));
        output.flush();
        continue;
      }

      try {
        Result result = interpreter.evaluate(inputLine);
        switch (result) {
          case ValueResult(Value value) -> output.println(value);
          case VoidResult() -> {}
          case ControlSignal signal ->
              throw new IllegalStateException("Signals are not supported. Provided: " + signal);
        }
      } catch (SyntaxException e) {
        errors.println("Syntax error: " + e);
        errors.flush();
      } catch (IllegalStateException e) {
        errors.println("Runtime error: " + e);
        errors.flush();
      } catch (ArithmeticException e) {
        errors.println("Arithmetic error: " + e);
        errors.flush();
      } catch (Exception e) {
        errors.println("Error: " + e);
        errors.flush();
      }
      output.flush();
    }
  }
}
