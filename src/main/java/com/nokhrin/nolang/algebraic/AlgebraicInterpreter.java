package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicLexer;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.errors.SyntaxException;
import com.nokhrin.nolang.common.executions.Result;
import com.nokhrin.nolang.common.executions.Scope;
import java.io.PrintStream;
import org.antlr.v4.runtime.*;

public class AlgebraicInterpreter {
  private final PrintStream output;
  private final Scope scope;

  public AlgebraicInterpreter(PrintStream output) {
    this.output = output;
    this.scope = new Scope(null);
  }

  public Result evaluate(String input) {
    CharStream inputStream = CharStreams.fromString(input);
    AlgebraicLexer lexer = new AlgebraicLexer(inputStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    AlgebraicParser parser = new AlgebraicParser(tokens);

    parser.removeErrorListeners();
    parser.addErrorListener(
        new BaseErrorListener() {
          @Override
          public void syntaxError(
              Recognizer<?, ?> recognizer,
              Object offendingSymbol,
              int line,
              int charPositionInLine,
              String msg,
              RecognitionException e) {
            throw new SyntaxException(
                "Line " + line + ":" + charPositionInLine + ":" + msg + "\n" + e);
          }
        });

    AlgebraicParser.ProgramContext tree = parser.program();

    return new AlgebraicEvalVisitor(scope).visit(tree);
  }
}
