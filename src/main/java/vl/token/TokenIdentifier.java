package vl.token;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import vl.operator.Operators;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenIdentifier {
    static boolean isNumber(char c) {
        return Character.isDigit(c) || c == '.';
    }

    static boolean isOpenParentheses(char c) {
        return c == '(' || c == '{' || c == '[';
    }

    static boolean isCloseParentheses(char c) {
        return c == ')' || c == '}' || c == ']';
    }

    static boolean isOperator(char c) {
        return Operators.isOperator(c);
    }

    static boolean isAlphabetic(char c) {
        return Character.isLetter(c) || c == '_';
    }

    static boolean isWhiteSpace(char c) {
        return Character.isWhitespace(c);
    }
}
