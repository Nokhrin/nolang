package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicLexer;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.Executor;
import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.values.Value;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class AlgebraicEvaluator implements Executor {
    private final PrintStream output;
    private final Scope globalScope;

    public AlgebraicEvaluator(PrintStream output) {
        this.output = output;
        this.globalScope = new Scope(null);
    }

    @Override
    public void runInteractive() {
        Scanner scanner=new Scanner(System.in);
        output.println("Algebraic Calculator REPL. Type 'exit' to quit.");
        while (true){
            output.print("> ");
            if (!scanner.hasNextLine()){
                break;
            }
            String input =scanner.nextLine();
            if (input.trim().equalsIgnoreCase("exit")){
                break;
            }
            if (input.trim().isEmpty()){
                continue;
            }
            try {
                Value result = evaluate(input);
                if (result!=null){
                    output.println(result);
                }
            }catch (Exception e){
                output.println("Error: " + e);
            }
            output.flush();
        }
        scanner.close();
    }

    @Override
    public void runFile(String path) {
        try {
            String content = Files.readString(Paths.get(path));
            Value result = evaluate(content);
            if (result!=null){
                output.println(result);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error on reading file: " + path, e);
        }
    }

    private Value evaluate(String input) {
        AlgebraicLexer lexer=new AlgebraicLexer(CharStreams.fromString(input));
        CommonTokenStream tokens=new CommonTokenStream(lexer);
        AlgebraicParser parser = new AlgebraicParser(tokens);
        AlgebraicParser.ProgContext tree =parser.prog();
        AlgebraicEvalVisitor visitor=new AlgebraicEvalVisitor(globalScope);
        return visitor.visit(tree);
    }
}
