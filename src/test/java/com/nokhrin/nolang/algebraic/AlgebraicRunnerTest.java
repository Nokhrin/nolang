package com.nokhrin.nolang.algebraic;

import static org.junit.jupiter.api.Assertions.*;

import com.nokhrin.nolang.AlgebraicRunner;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AlgebraicRunnerTest {
    private ByteArrayInputStream inputStream;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
    }

    @Test
    void repl_assignVar_printVar_callPrintVar_quit() {
        String src =
                """
                        x=1
                        x
                        print(x)
                        /q
                        """;
        inputStream = new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8));
        AlgebraicRunner.runWithStreams(
                new String[] {},
                inputStream,
                new PrintStream(outputStream),
                new PrintStream(errorStream));

        String output = outputStream.toString();
        assertAll(
                () -> assertTrue(output.contains("1")), () -> assertFalse(output.contains("x=1")));
    }

    @Test
    void repl_divByZero_stderrMessage_executionContinues() {
        String src =
                """
                1/0
                2+3
                /q
                """;
        inputStream = new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8));

        AlgebraicRunner.runWithStreams(
                new String[] {},
                inputStream,
                new PrintStream(outputStream),
                new PrintStream(errorStream));

        String errors = errorStream.toString();
        String output = outputStream.toString();
        assertAll(
                () -> assertTrue(errors.contains("Division by zero")),
                () -> assertTrue(output.contains("5")));
    }

    @Test
    void repl_emptyStream_stdoutEmpty() {
        String src = "";
        inputStream = new ByteArrayInputStream(src.getBytes());

        String output = outputStream.toString().replace("\r\n", "\n");
        assertFalse(output.contains("\n\n"));
    }

    @Test
    void repl_helpCommand_stdoutContainsUsage() {
        String src =
                """
                /h
                /q
                """;
        inputStream = new ByteArrayInputStream(src.getBytes());

        AlgebraicRunner.runWithStreams(
                new String[] {},
                inputStream,
                new PrintStream(outputStream),
                new PrintStream(errorStream));

        String output = outputStream.toString();
        assertAll(
                () -> assertTrue(output.contains("Algebraic Interpreter")),
                () -> assertTrue(output.contains("print")),
                () -> assertTrue(output.contains("abs")));
    }

    @Test
    void file_validStatements_stdoutMatchesExpected(@TempDir Path tempDir) throws IOException {
        Path inputFile = tempDir.resolve("statements.txt");
        Files.writeString(
                inputFile,
                """
                print(abs(-3))
                print(2!^3)
                print(5!)""");

        AlgebraicRunner.runWithStreams(
                new String[] {inputFile.toString()},
                System.in,
                new PrintStream(outputStream),
                new PrintStream(errorStream));

        String output = outputStream.toString().trim();
        assertEquals("3\n8\n120", output);
    }

    @Test
    void file_nonexistentPath_stderrMessage(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("does_not_exist.txt");

        AlgebraicRunner.runWithStreams(
                new String[] {nonExistent.toString()},
                System.in,
                new PrintStream(outputStream),
                new PrintStream(errorStream));

        String errorOutput = errorStream.toString();
        assertTrue(errorOutput.contains("File not found"));
    }
}
