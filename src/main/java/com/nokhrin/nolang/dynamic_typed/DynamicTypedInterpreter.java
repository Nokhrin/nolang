package com.nokhrin.nolang.dynamic_typed;

import com.nokhrin.nolang.DynamicTypedLexer;
import com.nokhrin.nolang.DynamicTypedParser;
import com.nokhrin.nolang.common.executions.FunctionRegistry;
import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.functional.Eval;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.PrintStream;

public class DynamicTypedInterpreter {
    private final Scope globalScope;
    private final FunctionRegistry functionRegistry;

    public DynamicTypedInterpreter(PrintStream output) {
        this.globalScope = new Scope();
        this.functionRegistry = new FunctionRegistry();
    }

    Eval<Value> evaluate(String input) {
        CharStream inputStream = CharStreams.fromString(input);
        DynamicTypedLexer lexer = new DynamicTypedLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DynamicTypedParser parser = new DynamicTypedParser(tokens);

        parser.removeErrorListeners();
        return null;
    }
}
