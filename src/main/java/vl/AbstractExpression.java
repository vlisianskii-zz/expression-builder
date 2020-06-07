package vl;

import lombok.Getter;
import vl.algorithms.TokenAlgorithm;
import vl.constant.Constants;
import vl.exception.InvalidExpressionException;
import vl.exception.InvalidTokenException;
import vl.exception.NotEnoughDataException;
import vl.function.Coordinates;
import vl.function.Function;
import vl.operator.Operator;
import vl.table.Result;
import vl.table.ValueTable;
import vl.token.tokens.ExpressionToken;
import vl.token.tokens.ArgumentToken;
import vl.token.tokens.ValueToken;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

@Getter
public class AbstractExpression<X, Y> {
    private final String name;
    private final String expression;
    private final List<ExpressionToken> tokens;
    private final Map<String, Double> constants;

    public AbstractExpression(String name, String expression, TokenAlgorithm<Integer, String> algorithm, Function<Integer, String>[] functions) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(expression);

        this.name = name;
        this.expression = expression;
        this.tokens = algorithm.tokenize(expression, functions);
        this.constants = stream(Constants.values())
                .collect(Collectors.toMap(Constants::name, Constants::getValue));
    }

    public Result<X, Y> calculate(Map<String, Double> customVariables) {
        return compute(null, null, null, customVariables);
    }

    public Result<X, Y> calculate() {
        return compute(null, null, null, Collections.emptyMap());
    }

    @SuppressWarnings("java:S1149")
    Result<X, Y> compute(ValueTable<X, Y> table, X x, Y y, Map<String, Double> customVariables) {
        checkTokens(tokens, customVariables);
        Stack<Double> output = new Stack<>();
        for (ExpressionToken token : tokens) {
            switch (token.getTokenType()) {
                case NUMBER:
                    applyNumber(output, token);
                    break;
                case OPERATOR:
                    applyOperator(output, token);
                    break;
                case FUNCTION: {
                    applyFunction(output, token, table, x, y);
                    break;
                }
                case VARIABLE:
                    applyVariable(output, token, table, x, y, customVariables);
                    break;
                default:
                    throw new InvalidTokenException("Unable to parse token: " + token);
            }
        }
        Double value = output.pop();
        if (!output.isEmpty()) {
            throw new InvalidExpressionException("Output queue is not empty: " + this);
        }
        return buildResult(x, y, value, name);
    }

    public Result<X, Y> buildResult(X x, Y y, Double value, String name) {
        return Result.<X, Y>builder()
                .x(x)
                .y(y)
                .value(value)
                .name(name)
                .build();
    }

    @SuppressWarnings("java:S1149")
    private void applyVariable(Stack<Double> output, ExpressionToken token, ValueTable<X, Y> table, X x, Y y, Map<String, Double> customVariables) {
        ValueToken<String> t = (ValueToken<String>) token;

        String tokenName = t.getValue();
        if (constants.containsKey(tokenName)) {
            output.push(constants.get(tokenName));
            return;
        }
        if (customVariables.containsKey(tokenName)) {
            output.push(customVariables.get(tokenName));
            return;
        }
        if (isNull(table)) {
            throw new NotEnoughDataException("Not enough data to compute expression: " + expression + ". Unable to find token: " + tokenName);
        }
        output.push(getValueFromTable(table, x, y, tokenName));
    }

    @SuppressWarnings("java:S1172")
    protected Double getValueFromTable(ValueTable<X, Y> table, X x, Y y, String tokenName) {
        return table.getValue(x, y);
    }

    @SuppressWarnings("java:S1149")
    private void applyFunction(Stack<Double> output, ExpressionToken token, ValueTable<X, Y> table, X x, Y y) {
        ArgumentToken<Function<X, Y>> t = (ArgumentToken<Function<X, Y>>) token;
        Coordinates<X, Y> coordinates = getCoordinates(x, y, t.getArguments());
        Function<X, Y> function = t.getValue();
        output.push(function.apply(t, table, coordinates));
    }

    @SuppressWarnings("java:S1172")
    protected Coordinates<X, Y> getCoordinates(X x, Y y, String arguments) {
        return Coordinates.<X, Y>builder()
                .x(x)
                .y(y)
                .build();
    }

    @SuppressWarnings("java:S1149")
    private void applyNumber(Stack<Double> output, ExpressionToken token) {
        ValueToken<Double> t = (ValueToken<Double>) token;
        output.push(t.getValue());
    }

    @SuppressWarnings("java:S1149")
    private void applyOperator(Stack<Double> output, ExpressionToken token) {
        ValueToken<Operator> t = (ValueToken<Operator>) token;
        Operator o = t.getValue();
        if (output.size() < o.getNumOperands()) {
            throw new InvalidExpressionException(String.format("Invalid number of operands available for operator '%s', expression [%s]", o.getSymbol(), this));
        }
        if (o.getNumOperands() == 2) {
            double rightArg = output.pop();
            double leftArg = output.pop();
            output.push(o.apply(leftArg, rightArg));
            return;
        }
        double arg = output.pop();
        output.push(o.apply(arg));
    }

    protected void checkTokens(List<ExpressionToken> tokens, Map<String, Double> customVariables) {
        // overridable
    }

    @Override
    public String toString() {
        return name + " = " + expression;
    }
}
