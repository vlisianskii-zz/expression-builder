package vl.token.tokens;

import lombok.Data;
import vl.token.TokenType;

@Data
public class SimpleToken implements ExpressionToken {
    private final TokenType tokenType;

    public SimpleToken(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public TokenType getTokenType() {
        return tokenType;
    }
}
