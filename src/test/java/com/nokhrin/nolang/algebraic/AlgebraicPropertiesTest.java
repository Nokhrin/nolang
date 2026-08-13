package com.nokhrin.nolang.algebraic;

import com.nokhrin.nolang.AlgebraicLexer;
import com.nokhrin.nolang.AlgebraicParser;
import com.nokhrin.nolang.common.executions.Scope;
import com.nokhrin.nolang.common.values.Value;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static org.testng.Assert.assertEquals;

public class AlgebraicPropertiesTest {
    private Value evaluate(String input) {
        Scope scope = new Scope(null);
        AlgebraicLexer lexer = new AlgebraicLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AlgebraicParser parser = new AlgebraicParser(tokens);
        AlgebraicParser.ProgContext tree = parser.prog();
        AlgebraicEvalVisitor visitor = new AlgebraicEvalVisitor(scope);

        return visitor.visit(tree);
    }

    @Property
    void additionIsCommutative(
            @ForAll int a,
            @ForAll int b
    ){
        assertEquals(evaluate(a+"+"+b), evaluate(b+"+"+a));
    }
}
