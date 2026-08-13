package com.nokhrin.nolang;

import com.nokhrin.nolang.algebraic.AlgebraicEvaluator;

import java.io.PrintStream;

/**
 * mvn clean package
 * java -jar target/nolang-1.0-SNAPSHOT.jar
 * java -jar target/nolang-1.0-SNAPSHOT.jar src/test/resources/algebraic/algebra.txt
 */
public class ReplAlgebraic {
    static void main(String[] args) {
        PrintStream output=System.out;
        AlgebraicEvaluator evaluator=new AlgebraicEvaluator(output);
        if (args.length==0){
            evaluator.runInteractive();
        }else if (args.length==1){
            evaluator.runFile(args[0]);
        }else {
            throw new IllegalArgumentException("Cannot process arguments provided");
        }
    }
}
