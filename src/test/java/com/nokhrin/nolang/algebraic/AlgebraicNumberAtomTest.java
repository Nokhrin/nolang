package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.combinators.BuiltInFunctions;
import com.nokhrin.nolang.common.core.*;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.Value;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AlgebraicNumberAtomTest {


    private AlgebraicParser.NumberAtomContext extractNumberCtx(String src) {
        Either<List<EvalError.SyntaxError>, AlgebraicParser.ProgramContext> parsedNumber = AlgebraicSyntaxAnalyzer.parse(src);

        if (parsedNumber.isLeft()) {

            fail("Error parsing " + src + " as number atom");
        }

        AlgebraicParser.ProgramContext programContext = parsedNumber.rightOptional().orElseThrow();

        if (programContext instanceof AlgebraicParser.ProgramWithStatementsContext pwsc) {
            var stmt = pwsc.statement(0);
            var assign = stmt.assignment();
            if (assign instanceof AlgebraicParser.TermStatementContext tsc) {
                var term = tsc.term();
                var factor = term.factor(0);
                var unary = factor.unary(0);
                if (unary instanceof AlgebraicParser.PostfixExpressionContext pec) {
                    var postfix = pec.postfix();
                    var atom = postfix.atom();
                    if (atom instanceof AlgebraicParser.NumberAtomContext nac) {
                        return nac;
                    }
                }
            }

        }
        throw new AssertionError("NumberContext not found");


    }

    @Test
    void zeroParsedAsNumberAtom() {
        // Получить NumberAtomContext
        AlgebraicParser.NumberAtomContext numberAtomContext = extractNumberCtx("0");

        //Создать пустой ExecutionContext
        Scope scope = new Scope();
        FunctionRegistry registry = new FunctionRegistry(BuiltInFunctions.create());
        ExecutionContext context = new ExecutionContext(scope, registry, List.of());

        //Вызвать visitNumberAtom
        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor();
        Eval<Value> number = visitor.visitNumberAtom(numberAtomContext);

        //Выполнить вычисление
        EvalResult<Value> result = number.run(context);

        //Проверить результат
        switch (result) {
            case EvalResult.Returned<Value> returned -> {
                assertEquals(new NumericValue.IntValue(0), returned.value());
                assertEquals("0", numberAtomContext.NUM().getText());
            }
            case EvalResult.Interrupted<Value> interrupted ->
                fail("Unexpected interruption: " + interrupted.reason().message());
        }

    }
}
