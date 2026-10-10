package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.core.EvalResult;
import com.nokhrin.nolang.common.core.ExecutionContext;
import com.nokhrin.nolang.common.values.Value;
import java.util.stream.Collectors;

public interface AlgebraicInterpreter {

    Eval<Value> compile(String source);

    static AlgebraicInterpreter create() {
        return source ->
                AlgebraicSyntaxAnalyzer.parse(source)
                        .fold(
                                syntaxErrors ->
                                        Eval.raiseError(
                                                new EvalError.SyntaxError(
                                                        syntaxErrors.stream()
                                                                .map(EvalError.SyntaxError::message)
                                                                .collect(
                                                                        Collectors.joining(
                                                                                System
                                                                                        .lineSeparator())))),
                                tree -> new AlgebraicEvalVisitor().visit(tree));
    }

    default EvalResult<Value> evaluate(String source, ExecutionContext executionContext) {
        return compile(source).run(executionContext);
    }
}
