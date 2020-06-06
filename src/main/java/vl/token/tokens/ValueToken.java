package vl.token.tokens;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import vl.token.TokenType;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ValueToken<T> extends SimpleToken {
    private final T value;

    public ValueToken(TokenType tokenType, T value) {
        super(tokenType);
        this.value = value;
    }
}
