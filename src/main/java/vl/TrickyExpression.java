package vl;

import vl.algorithms.TokenAlgorithm;
import vl.exception.InvalidExpressionException;
import vl.exception.NotEnoughDataException;
import vl.function.Function;
import vl.table.Result;
import vl.table.ValueTable;
import vl.token.TokenType;
import vl.token.tokens.ArgumentToken;
import vl.token.tokens.ExpressionToken;
import vl.token.tokens.ValueToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TrickyExpression extends AbstractExpression<Integer, String> {
    private static final String RESERVED_VARIABLE = "self";

    public TrickyExpression(String name, String expression, TokenAlgorithm<Integer, String> algorithm) {
        super(name, expression, algorithm, null);
    }

    public TrickyExpression(String name, String expression, TokenAlgorithm<Integer, String> algorithm, Function<Integer, String>[] functions) {
        super(name, expression, algorithm, functions);
    }

    public Result<Integer, String> calculate(ValueTable<Integer, String> table, Integer x, String y) {
        return calculate(table, x, y, Collections.emptyMap());
    }

    public Result<Integer, String> calculate(ValueTable<Integer, String> table, Integer x, String y, Map<String, Double> customVariables) {
        try {
            return super.compute(table, x, y, customVariables);
        } catch (NotEnoughDataException e) {
            Double value = table.getValue(x, y);
            return buildResult(x, y, value, getName());
        }
    }

    @Override
    protected void checkTokens(List<ExpressionToken> tokens, Map<String, Double> customVariables) {
        if (!tokens.stream()
                .filter(t -> t.getTokenType().equals(TokenType.VARIABLE) || t.getTokenType().equals(TokenType.FUNCTION))
                .map(t -> {
                    if (t.getTokenType().equals(TokenType.VARIABLE)) {
                        return ((ValueToken<String>) t).getValue();
                    }
                    return ((ArgumentToken<String>) t).getArguments();
                })
                .filter(v -> !customVariables.containsKey(v))
                .allMatch(RESERVED_VARIABLE::equals)) {
            throw new InvalidExpressionException(String.format("All VARIABLE tokens must be reserved variable '%s' for this type of expression", RESERVED_VARIABLE));
        }
    }
}
