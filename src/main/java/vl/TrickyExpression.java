package vl;

import vl.algorithms.TokenAlgorithm;
import vl.exception.InvalidExpressionException;
import vl.exception.NotEnoughDataException;
import vl.function.Coordinates;
import vl.function.Function;
import vl.table.Result;
import vl.table.ValueTable;
import vl.token.Token;
import vl.token.TokenType;

import java.util.List;
import java.util.Map;

public class TrickyExpression extends AbstractExpression<Integer, String> {
    private String tokenName;

    public TrickyExpression(String name, String expression, TokenAlgorithm<Integer, String> algorithm, Function<Integer, String>[] functions) {
        super(name, expression, algorithm, functions);
    }

    protected Result<Integer, String> calculate(ValueTable<Integer, String> table, Integer x, Map<String, Double> customVariables) {
        try {
            clearTokens();
            return super.compute(table, x, null, customVariables);
        } catch (NotEnoughDataException e) {
            Double value = table.getValue(x, tokenName);
            return buildResult(x, tokenName, value, tokenName);
        } finally {
            clearTokens();
        }
    }

    @Override
    protected Double getValueFromTable(ValueTable<Integer, String> table, Integer x, String y, String tokenName) {
        this.tokenName = tokenName;
        return table.getValue(x, tokenName);
    }

    @Override
    protected Coordinates<Integer, String> getCoordinates(Integer x, String y, String arguments) {
        this.tokenName = arguments;
        return Coordinates.<Integer, String>builder()
                .x(x)
                .y(tokenName)
                .build();
    }

    @Override
    public Result<Integer, String> buildResult(Integer x, String y, Double value, String name) {
        return Result.<Integer, String>builder()
                .x(x)
                .y(tokenName)
                .value(value)
                .name(tokenName)
                .build();
    }

    @Override
    protected void checkTokens(List<Token<Object>> tokens, Map<String, Double> customVariables) {
        if (tokens.stream()
                .filter(t -> t.getTokenType().equals(TokenType.VARIABLE) || t.getTokenType().equals(TokenType.FUNCTION))
                .map(t -> {
                    if (t.getTokenType().equals(TokenType.VARIABLE)) {
                        return (String)t.getValue();
                    }
                    return t.getArguments();
                })
                .filter(v -> !customVariables.containsKey(v))
                .distinct()
                .count() > 1) {
            throw new InvalidExpressionException("All VARIABLE tokens must be distinct for this type of expression");
        }
    }

    private void clearTokens() {
        tokenName = null;
    }
}
