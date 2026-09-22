package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.core.Environment;
import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.core.Result;
import com.nokhrin.nolang.common.values.Value;
import java.util.stream.Collectors;

public interface AlgebraicInterpreter {

  Eval<Value> compile(String source);

  static AlgebraicInterpreter monadic() {
    return source ->
        AlgebraicSyntaxAnalyzer.parse(source)
            .fold(
                syntaxErrors ->
                    Eval.raiseError(
                        new EvalError.SyntaxError(
                            syntaxErrors.stream()
                                .map(EvalError.SyntaxError::message)
                                .collect(Collectors.joining(System.lineSeparator())))),
                tree -> new AlgebraicEvalVisitor().visit(tree));
  }

  default Result<Value> interpret(String source, Environment environment) {
    return compile(source).run(environment);
  }
}
