package vl.token;

import lombok.Data;

@Data
public class Token<T> {
    private final T value;
    private final TokenType tokenType;
    private final String arguments;

    Token(TokenType type) {
        this(null, type, null);
    }

    Token(T value, TokenType type) {
        this(value, type, null);
    }

    Token(T value, TokenType type, String arguments) {
        this.value = value;
        this.tokenType = type;
        this.arguments = arguments;
    }
}
