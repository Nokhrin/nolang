package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicLexer;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.common.core.Either;
import com.nokhrin.nolang.common.core.Environment;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.core.Result;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlgebraicInterpreter {

    public Either<List<EvalError.SyntaxError>, AlgebraicParser.ProgramContext> parseProgram(String input) {
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
                    syntaxErrors.add(new EvalError.SyntaxError("Line " + line + ":" + column + " - " + message));
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

        if (syntaxErrors.isEmpty()) {
            return Either.right(tree);
        }
        return Either.left(syntaxErrors);
    }

    public Result<Value> evaluate(String input, Environment environment) {
        Either<List<EvalError.SyntaxError>, AlgebraicParser.ProgramContext> parsedProgram = parseProgram(input);

        return parsedProgram
            .fold(
                syntaxErrors -> new Result.Failure<>(
                    environment,
                    new EvalError.SyntaxError(syntaxErrors.stream()
                        .map(EvalError.SyntaxError::message)
                        .collect(Collectors.joining(System.lineSeparator())))
                ),
                tree -> new AlgebraicEvalVisitor().visit(tree).run(environment)
            );


    }
}
