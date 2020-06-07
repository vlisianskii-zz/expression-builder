package vl.token;

import vl.exception.InvalidTokenException;
import vl.function.Function;
import vl.operator.Operator;
import vl.operator.Operators;
import vl.token.tokens.ExpressionToken;
import vl.token.tokens.ArgumentToken;
import vl.token.tokens.SimpleToken;
import vl.token.tokens.ValueToken;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

public class TokenIterator<X, Y> implements Iterator<ExpressionToken> {
    private final char[] expression;
    private final Map<String, Function<X, Y>> functions;

    private int pointer = 0;
    private ExpressionToken lastToken;

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
    public ExpressionToken next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        char c = skipWhiteSpaces();
        ExpressionToken token = getNextToken(c);
        lastToken = token;
        return token;
    }

    private ExpressionToken getNextToken(char c) {
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

    private ExpressionToken functionOrVariable() {
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
                return new ArgumentToken<>(TokenType.FUNCTION, f, sb.substring(1, sb.length() - 1));
            }
            length++;
            pointer++;
        }
        return new ValueToken<>(TokenType.VARIABLE, name);
    }

    private ExpressionToken operator(char c) {
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
                        Operator lastOperator = ((ValueToken<Operator>) lastToken).getValue();
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
        return new ValueToken<>(TokenType.OPERATOR, lastValid);
    }

    private ExpressionToken number(char c) {
        if (lastToken != null && lastToken.getTokenType().equals(TokenType.NUMBER)) {
            throw new InvalidTokenException(String.format("Unable to parse char '%s' at [%s]", c, pointer));
        }
        int offset = pointer;
        int length = 1;
        pointer++;
        if (isEnd(offset + length)) {
            double value = Double.parseDouble(String.valueOf(expression, offset, length));
            return new ValueToken<>(TokenType.NUMBER, value);
        }
        while (!isEnd(offset + length) && TokenIdentifier.isNumber(expression[offset + length])) {
            length++;
            pointer++;
        }
        double value = Double.parseDouble(String.valueOf(expression, offset, length));
        return new ValueToken<>(TokenType.NUMBER, value);
    }

    private ExpressionToken openParentheses() {
        pointer++;
        return new SimpleToken(TokenType.PARENTHESES_OPEN);
    }

    private ExpressionToken closeParentheses() {
        pointer++;
        return new SimpleToken(TokenType.PARENTHESES_CLOSE);
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
