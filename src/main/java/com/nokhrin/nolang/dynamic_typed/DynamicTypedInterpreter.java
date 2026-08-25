package com.nokhrin.nolang.dynamic_typed;

import com.nokhrin.nolang.DynamicTypedLexer;
import com.nokhrin.nolang.DynamicTypedParser;
import com.nokhrin.nolang.common.errors.SyntaxException;
import com.nokhrin.nolang.common.executions.FunctionRegistry;
import com.nokhrin.nolang.common.executions.Result;
import com.nokhrin.nolang.common.executions.Scope;
import org.antlr.v4.runtime.*;

import java.io.PrintStream;

public class DynamicTypedInterpreter {
    private final Scope globalScope;
    private final FunctionRegistry functionRegistry;

    public DynamicTypedInterpreter(PrintStream output) {
        this.globalScope = new Scope();
        this.functionRegistry = new FunctionRegistry(output);
    }

    Result evaluate(String input) {
        CharStream inputStream = CharStreams.fromString(input);
        DynamicTypedLexer lexer = new DynamicTypedLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DynamicTypedParser parser = new DynamicTypedParser(tokens);

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

        DynamicTypedParser.ProgramContext tree = parser.program();

        return new DynamicTypedEvalVisitor(globalScope, functionRegistry).visit(tree);
    }
}
