package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicLexer;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.EvalError;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;

public class AlgebraicSyntaxAnalyzer {

  public static Either<List<EvalError.SyntaxError>, AlgebraicParser.ProgramContext> parse(
      String input) {
    CharStream inputStream = CharStreams.fromString(input);

    List<EvalError.SyntaxError> syntaxErrors = new ArrayList<>();
    BaseErrorListener errorListener =
        new BaseErrorListener() {
          @Override
          public void syntaxError(
              Recognizer<?, ?> recognizer,
              Object offendingSymbol,
              int line,
              int column,
              String message,
              RecognitionException e) {
            syntaxErrors.add(
                new EvalError.SyntaxError("Line " + line + ":" + column + " - " + message));
          }
        };

    AlgebraicLexer lexer = new AlgebraicLexer(inputStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    AlgebraicParser parser = new AlgebraicParser(tokens);

    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    AlgebraicParser.ProgramContext tree = parser.program();

    return syntaxErrors.isEmpty() ? Either.right(tree) : Either.left(List.copyOf(syntaxErrors));
  }
}
