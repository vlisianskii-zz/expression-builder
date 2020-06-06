package vl.token.tokens;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import vl.token.TokenType;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ArgumentToken<T> extends ValueToken<T> {
    private final String arguments;

    public ArgumentToken(TokenType tokenType, T value, String arguments) {
        super(tokenType, value);
        this.arguments = arguments;
    }
}
