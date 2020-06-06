package vl.token;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import vl.exception.InvalidTokenException;
import vl.operator.Operators;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
public class TokenIteratorTest {
    @Test
    public void return_tokens_when_simple_sum_expression() {
        assertTokenIterator("7 + 3", new Token[]{
                Token.create(7.0, TokenType.NUMBER),
                getOperator('+', 2),
                Token.create(3.0, TokenType.NUMBER)
        });
    }

    @Test
    public void return_tokens_when_simple_sum_expression_and_no_spaces() {
        assertTokenIterator("1-0.2", new Token[]{
                Token.create(1.0, TokenType.NUMBER),
                getOperator('-', 2),
                Token.create(0.2, TokenType.NUMBER)
        });
    }

    @Test
    public void return_tokens_when_simple_sum_of_doubles_expression() {
        assertTokenIterator("1 * 2.3 / 4.42", new Token[]{
                Token.create(1.0, TokenType.NUMBER),
                getOperator('*', 2),
                Token.create(2.3, TokenType.NUMBER),
                getOperator('/', 2),
                Token.create(4.42, TokenType.NUMBER)
        });
    }

    @Test
    public void return_tokens_when_unary_operation() {
        assertTokenIterator("-8 + (+3)", new Token[]{
                getOperator('-', 1),
                Token.create(8.0, TokenType.NUMBER),
                getOperator('+', 2),
                Token.create(TokenType.PARENTHESES_OPEN),
                getOperator('+', 1),
                Token.create(3.0, TokenType.NUMBER),
                Token.create(TokenType.PARENTHESES_CLOSE)
        });
    }

    @Test
    public void return_tokens_when_unary_operation_without_parentheses() {
        assertTokenIterator("-8 +-3", new Token[]{
                getOperator('-', 1),
                Token.create(8.0, TokenType.NUMBER),
                getOperator('+', 2),
                getOperator('-', 1),
                Token.create(3.0, TokenType.NUMBER)
        });
    }

    @Test(expected = InvalidTokenException.class)
    public void throw_exception_when_no_operator() {
        assertTokenIterator("1 2", null);
    }

    @Test
    public void return_tokens_when_operation_in_parentheses() {
        assertTokenIterator("(A*B)/ 3", new Token[]{
                Token.create(TokenType.PARENTHESES_OPEN),
                Token.create("A", TokenType.VARIABLE),
                getOperator('*', 2),
                Token.create("B", TokenType.VARIABLE),
                Token.create(TokenType.PARENTHESES_CLOSE),
                getOperator('/', 2),
                Token.create(3.0, TokenType.NUMBER)
        });
    }

    private Token<Object> getOperator(char c, int numArguments) {
        return Token.create(Operators.getOperator(c, numArguments), TokenType.OPERATOR);
    }

    private void assertTokenIterator(String expression, Token<Object>[] tokens) {
        TokenIterator<Integer, String> iterator = new TokenIterator<>(expression, null);

        List<Object> actualTokens = ImmutableList.copyOf(iterator);

        assertThat(actualTokens).isNotNull();
        assertThat(actualTokens).containsExactly(tokens);
    }
}
