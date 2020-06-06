package vl.token;

import lombok.AllArgsConstructor;
import vl.exception.InvalidTokenException;
import vl.function.Function;
import vl.operator.Operator;
import vl.operator.Operators;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

@AllArgsConstructor
public class TokenIterator<X, Y> implements Iterator<Token<Object>> {
    private final char[] expression;
    private final Map<String, Function<X, Y>> functions;

    private int pointer = 0;
    private Token<Object> lastToken;

    public TokenIterator(String expression, Function<X, Y>[] functions) {
        this.expression = expression.trim().toCharArray();
        this.functions = isNull(functions) ?
                Collections.emptyMap() :
                stream(functions).collect(Collectors.toMap(Function::getName, v -> v));
    }

    @Override
    public boolean hasNext() {
        return !isEnd(pointer);
    }

    @Override
    public Token<Object> next() {
        char c = skipWhiteSpaces();
        Token<Object> token = getNextToken(c);
        lastToken = token;
        return token;
    }

    private Token<Object> getNextToken(char c) {
        if (TokenIdentifier.isNumber(c)) {
            return number(c);
        } else if (TokenIdentifier.isOpenParentheses(c)) {
            return openParentheses();
        } else if (TokenIdentifier.isCloseParentheses(c)) {
            return closeParentheses();
        } else if (TokenIdentifier.isOperator(c)) {
            return operator(c);
        } else if (TokenIdentifier.isAlphabetic(c)) {
            return functionOrVariable();
        }
        throw new InvalidTokenException(String.format("Unable to parse char '%s' at [%s]", c, pointer));
    }

    private Token<Object> functionOrVariable() {
        int offset = pointer;
        int length = 1;

        String name = null;
        while (!isEnd(pointer) && TokenIdentifier.isAlphabetic(expression[pointer])) {
            name = String.valueOf(expression, offset, length);
            if (functions.containsKey(name)) {
                Function<X, Y> f = functions.get(name);
                StringBuilder sb = new StringBuilder();
                while (!TokenIdentifier.isCloseParentheses(expression[pointer])) {
                    sb.append(expression[++pointer]);
                }
                pointer++;
                return Token.create(f, TokenType.FUNCTION, sb.substring(1, sb.length() - 1));
            }
            length++;
            pointer++;
        }
        return Token.create(name, TokenType.VARIABLE);
    }

    private Token<Object> operator(char c) {
        int offset = pointer;
        int length = 1;
        StringBuilder sb = new StringBuilder();
        Operator lastValid = null;
        sb.append(c);

        while (!isEnd(offset + length) && Operators.isOperator(expression[offset + length])) {
            sb.append(expression[offset + length++]);
        }

        while (sb.length() > 0) {
            Operator operator = null;
            if (sb.length() == 1) {
                int argc = 2;
                if (lastToken == null) {
                    argc = 1;
                } else {
                    TokenType lastTokenType = lastToken.getTokenType();
                    if (lastTokenType.equals(TokenType.OPERATOR)) {
                        Operator lastOperator = (Operator) lastToken.getValue();
                        if (lastOperator.getNumOperands() == 2 || (lastOperator.getNumOperands() == 1 && !lastOperator.isLeftAssociative())) {
                            argc = 1;
                        }
                    } else if (lastTokenType.equals(TokenType.PARENTHESES_OPEN)) {
                        argc = 1;
                    }
                }
                operator = Operators.getOperator(sb.charAt(0), argc);
            }
            if (isNull(operator)) {
                sb.setLength(sb.length() - 1);
            } else {
                lastValid = operator;
                break;
            }
        }

        pointer += sb.length();
        return Token.create(lastValid, TokenType.OPERATOR);
    }

    private Token<Object> number(char c) {
        if (lastToken != null && lastToken.getTokenType().equals(TokenType.NUMBER)) {
            throw new InvalidTokenException(String.format("Unable to parse char '%s' at [%s]", c, pointer));
        }
        int offset = pointer;
        int length = 1;
        pointer++;
        if (isEnd(offset + length)) {
            double value = Double.parseDouble(String.valueOf(expression, offset, length));
            return Token.create(value, TokenType.NUMBER);
        }
        while (!isEnd(offset + length) && TokenIdentifier.isNumber(expression[offset + length])) {
            length++;
            pointer++;
        }
        double value = Double.parseDouble(String.valueOf(expression, offset, length));
        return Token.create(value, TokenType.NUMBER);
    }

    private Token<Object> openParentheses() {
        pointer++;
        return Token.create(TokenType.PARENTHESES_OPEN);
    }

    private Token<Object> closeParentheses() {
        pointer++;
        return Token.create(TokenType.PARENTHESES_CLOSE);
    }

    private char skipWhiteSpaces() {
        char c = expression[pointer];
        while (TokenIdentifier.isWhiteSpace(c)) {
            c = expression[++pointer];
        }
        return c;
    }

    private boolean isEnd(int p) {
        return expression.length <= p;
    }
}
