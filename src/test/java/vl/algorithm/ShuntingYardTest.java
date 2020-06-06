package vl.algorithm;

import org.junit.Before;
import org.junit.Test;
import vl.algorithms.ShuntingYard;
import vl.algorithms.TokenAlgorithm;
import vl.operator.Operators;
import vl.token.TokenType;
import vl.token.tokens.ExpressionToken;
import vl.token.tokens.ValueToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ShuntingYardTest {
    private TokenAlgorithm<Integer, String> algorithm;

    @Before
    public void before() {
        algorithm = new ShuntingYard<>();
    }

    @Test
    public void return_correct_order_of_tokens() {
        // setup
        String expression = "1+(-2/3)*4";
        // action
        List<ExpressionToken> tokens = algorithm.tokenize(expression, null);
        // verify
        assertThat(tokens).containsExactly(
                getNumber(1.0),
                getNumber(2.0),
                getOperator('-', 1),
                getNumber(3.0),
                getOperator('/', 2),
                getNumber(4.0),
                getOperator('*', 2),
                getOperator('+', 2)
        );
    }

    private ExpressionToken getNumber(double value) {
        return new ValueToken<>(TokenType.NUMBER, value);
    }

    private ExpressionToken getOperator(char c, int numArguments) {
        return new ValueToken<>(TokenType.OPERATOR, Operators.getOperator(c, numArguments));
    }
}
