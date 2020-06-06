package vl.algorithms;

import vl.exception.InvalidTokenException;
import vl.exception.MismatchParenthesesException;
import vl.function.Function;
import vl.operator.Operator;
import vl.token.TokenIterator;
import vl.token.TokenType;
import vl.token.tokens.ExpressionToken;
import vl.token.tokens.ValueToken;

import java.util.*;

public class ShuntingYard<X, Y> implements TokenAlgorithm<X, Y> {
    @Override
    public List<ExpressionToken> tokenize(String expression, Function<X, Y>[] functions) {
        Stack<ExpressionToken> stack = new Stack<>();
        List<ExpressionToken> queue = new ArrayList<>();

        Iterator<ExpressionToken> iterator = new TokenIterator<>(expression, functions);
        while (iterator.hasNext()) {
            ExpressionToken token = iterator.next();
            switch (token.getTokenType()) {
                case NUMBER:
                case VARIABLE:
                case FUNCTION:
                    queue.add(token);
                    break;
                case OPERATOR:
                    while (!stack.isEmpty() && stack.peek().getTokenType().equals(TokenType.OPERATOR)) {
                        Operator o1 = ((ValueToken<Operator>) token).getValue();
                        Operator o2 = ((ValueToken<Operator>) stack.peek()).getValue();
                        if (o1.getNumOperands() == 1 && o2.getNumOperands() == 2) {
                            break;
                        } else if ((o1.isLeftAssociative() && o1.getPrecedence() <= o2.getPrecedence()) || o1.getPrecedence() < o2.getPrecedence()) {
                            queue.add(stack.pop());
                        } else {
                            break;
                        }
                    }
                    stack.push(token);
                    break;
                case PARENTHESES_OPEN:
                    stack.push(token);
                    break;
                case PARENTHESES_CLOSE:
                    try {
                        while (!stack.peek().getTokenType().equals(TokenType.PARENTHESES_OPEN)) {
                            queue.add(stack.pop());
                        }
                        stack.pop();
                        if (!stack.isEmpty() && stack.peek().getTokenType().equals(TokenType.FUNCTION)) {
                            queue.add(stack.pop());
                        }
                    } catch (EmptyStackException e) {
                        throw new MismatchParenthesesException(expression);
                    }
                    break;
                default:
                    throw new InvalidTokenException("Unable to parse token: " + token);
            }
        }

        while (!stack.empty()) {
            ExpressionToken t = stack.pop();
            if (t.getTokenType().equals(TokenType.PARENTHESES_CLOSE) || t.getTokenType().equals(TokenType.PARENTHESES_OPEN)) {
                throw new MismatchParenthesesException(expression);
            } else {
                queue.add(t);
            }
        }
        return queue;
    }
}
