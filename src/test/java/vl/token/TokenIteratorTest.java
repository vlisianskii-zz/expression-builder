package vl.token;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import vl.exception.InvalidTokenException;
import vl.function.functions.AvgFunction;
import vl.function.Function;
import vl.function.functions.NextFunction;
import vl.operator.Operators;
import vl.token.tokens.ArgumentToken;
import vl.token.tokens.ExpressionToken;
import vl.token.tokens.SimpleToken;
import vl.token.tokens.ValueToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenIteratorTest {
    @Test
    public void return_tokens_when_simple_sum_expression() {
        assertTokenIterator("7 + 3", new ExpressionToken[]{
                getNumber(7.0),
                getOperator('+', 2),
                getNumber(3.0)
        });
    }

    @Test
    public void return_tokens_when_simple_sum_expression_and_no_spaces() {
        assertTokenIterator("1-0.2", new ExpressionToken[]{
                getNumber(1.0),
                getOperator('-', 2),
                getNumber(0.2)
        });
    }

    @Test
    public void return_tokens_when_simple_sum_of_doubles_expression() {
        assertTokenIterator("1 * 2.3 / 4.42", new ExpressionToken[]{
                getNumber(1.0),
                getOperator('*', 2),
                getNumber(2.3),
                getOperator('/', 2),
                getNumber(4.42)
        });
    }

    @Test
    public void return_tokens_when_unary_operation() {
        assertTokenIterator("-8 + (+3)", new ExpressionToken[]{
                getOperator('-', 1),
                getNumber(8.0),
                getOperator('+', 2),
                getOpenParentheses(),
                getOperator('+', 1),
                getNumber(3.0),
                getCloseParentheses()
        });
    }

    @Test
    public void return_tokens_when_unary_operation_without_parentheses() {
        assertTokenIterator("-8 +-3", new ExpressionToken[]{
                getOperator('-', 1),
                getNumber(8.0),
                getOperator('+', 2),
                getOperator('-', 1),
                getNumber(3.0)
        });
    }

    @Test(expected = InvalidTokenException.class)
    public void throw_exception_when_no_operator() {
        assertTokenIterator("1 2", null);
    }

    @Test(expected = InvalidTokenException.class)
    public void throw_exception_when_unsupported_token() {
        assertTokenIterator("1+&", null);
    }

    @Test
    public void return_tokens_with_variables() {
        assertTokenIterator("(A*B)/ 3", new ExpressionToken[]{
                getOpenParentheses(),
                getVariable("A"),
                getOperator('*', 2),
                getVariable("B"),
                getCloseParentheses(),
                getOperator('/', 2),
                getNumber(3.0)
        });
    }

    @Test
    public void return_tokens_with_functions() {
        Function<Integer, String> function = new NextFunction();
        assertTokenIterator("(next(B)/next(A))", new ExpressionToken[]{
                        getOpenParentheses(),
                        getFunction(function, "B"),
                        getOperator('/', 2),
                        getFunction(function,"A"),
                        getCloseParentheses()
                },
                new Function[]{function});
    }

    @Test
    public void return_tokens_with_avg_functions() {
        Function<Integer, String> function = new AvgFunction();
        assertTokenIterator("avg(B,2)", new ExpressionToken[]{
                        getFunction(function, "B,2")
                },
                new Function[]{function});
    }

    private ExpressionToken getFunction(Function<Integer, String> function, String argument) {
        return new ArgumentToken<>(TokenType.FUNCTION, function, argument);
    }

    private ExpressionToken getOpenParentheses() {
        return new SimpleToken(TokenType.PARENTHESES_OPEN);
    }

    private ExpressionToken getCloseParentheses() {
        return new SimpleToken(TokenType.PARENTHESES_CLOSE);
    }

    private ExpressionToken getVariable(String value) {
        return new ValueToken<>(TokenType.VARIABLE, value);
    }

    private ExpressionToken getNumber(double value) {
        return new ValueToken<>(TokenType.NUMBER, value);
    }

    private ExpressionToken getOperator(char c, int numArguments) {
        return new ValueToken<>(TokenType.OPERATOR, Operators.getOperator(c, numArguments));
    }

    private void assertTokenIterator(String expression, ExpressionToken[] tokens) {
        assertTokenIterator(expression, tokens, null);
    }

    private void assertTokenIterator(String expression, ExpressionToken[] tokens, Function[] functions) {
        TokenIterator<Integer, String> iterator = new TokenIterator<>(expression, functions);

        List<ExpressionToken> actualTokens = ImmutableList.copyOf(iterator);

        assertThat(actualTokens).isNotNull();
        assertThat(actualTokens).containsExactly(tokens);
    }
}
