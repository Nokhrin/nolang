package com.nokhrin.nolang.algebraic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.EvalError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AlgebraicSyntaxAnalyzerTest {
  @ParameterizedTest
  @ValueSource(strings = {"src/test/resources/algebraic/statements_valid.txt"})
  void parse_validStatementsFromFile_returnsRight(String filePath) throws IOException {
    String content = Files.readString(Path.of(filePath));
    for (String line : content.split("\r?\n")) {
      if (!line.trim().isEmpty()) {
        Either<List<EvalError.SyntaxError>, ?> result = AlgebraicSyntaxAnalyzer.parse(line);
        assertTrue(result.isRight());
      }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"src/test/resources/algebraic/statements_invalid.txt"})
  void parse_invalidStatementsFromFile_returnsLeft(String filePath) throws IOException {
    String content = Files.readString(Path.of(filePath));
    for (String line : content.split("\r?\n")) {
      if (!line.trim().isEmpty()) {
        Either<List<EvalError.SyntaxError>, ?> result = AlgebraicSyntaxAnalyzer.parse(line);
        assertTrue(result.isLeft());
      }
    }
  }
}
