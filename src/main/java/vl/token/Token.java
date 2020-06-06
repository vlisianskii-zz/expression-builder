package vl.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Token<T> {
    private final T value;
    private final TokenType tokenType;
    private final String arguments;

    public static <T> Token<T> create(TokenType type) {
        return new Token<>(null, type, null);
    }

    public static <T> Token<T> create(T value, TokenType type) {
        return new Token<>(value, type, null);
    }

    public static <T> Token<T> create(T value, TokenType type, String arguments) {
        return new Token<>(value, type, arguments);
    }
}
