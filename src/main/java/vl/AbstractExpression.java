package vl;

import lombok.Getter;
import vl.algorithms.TokenAlgorithm;
import vl.constant.Constants;
import vl.exception.InvalidExpressionException;
import vl.exception.InvalidTokenException;
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

    public Result<X, Y> calculate() {
        return compute(null, null, null, Collections.emptyMap());
    }

    Result<X, Y> compute(ValueTable<X, Y> table, X x, Y y, Map<String, Double> customVariables) {
        checkTokens(tokens, customVariables);

        Stack<Double> output = new Stack<>();
        for (ExpressionToken token : tokens) {
            switch (token.getTokenType()) {
                case NUMBER: {
                    ValueToken<Double> t = (ValueToken<Double>) token;
                    output.push(applyNumber(t.getValue()));
                    break;
                }
                case OPERATOR: {
                    ValueToken<Operator> t = (ValueToken<Operator>) token;
                    output.push(applyOperator(t.getValue(), output));
                    break;
                }
                case FUNCTION: {
                    ArgumentToken<Function<X, Y>> t = (ArgumentToken<Function<X, Y>>) token;
                    output.push(applyFunction(t, table, x, y));
                    break;
                }
                case VARIABLE: {
                    ValueToken<String> t = (ValueToken<String>) token;
                    output.push(applyVariable(t, table, x, y, customVariables));
                    break;
                }
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

    private Double applyVariable(ValueToken<String> token, ValueTable<X, Y> table, X x, Y y, Map<String, Double> customVariables) {
        String tokenName = token.getValue();
        if (constants.containsKey(tokenName)) {
            return constants.get(tokenName);
        }
        if (customVariables.containsKey(tokenName)) {
            return customVariables.get(tokenName);
        }
        return getValueFromTable(table, x, y, tokenName);
    }

    protected Double getValueFromTable(ValueTable<X, Y> table, X x, Y y, String tokenName) {
        return table.getValue(x, y);
    }

    private Double applyFunction(ArgumentToken<Function<X, Y>> token, ValueTable<X, Y> table, X x, Y y) {
        Coordinates<X, Y> coordinates = getCoordinates(x, y, token.getArguments());
        Function<X, Y> function = token.getValue();
        return function.apply(token, table, coordinates);
    }

    protected Coordinates<X, Y> getCoordinates(X x, Y y, String arguments) {
        return Coordinates.<X, Y>builder()
                .x(x)
                .y(y)
                .build();
    }

    private Double applyNumber(Double v) {
        return v;
    }

    private Double applyOperator(Operator o, Stack<Double> output) {
        if (output.size() < o.getNumOperands()) {
            throw new InvalidExpressionException(String.format("Invalid number of operands available for operator '%s', expression [%s]", o.getSymbol(), this));
        }
        if (o.getNumOperands() == 2) {
            double rightArg = output.pop();
            double leftArg = output.pop();
            return o.apply(leftArg, rightArg);
        }
        double arg = output.pop();
        return o.apply(arg);
    }

    protected void checkTokens(List<ExpressionToken> tokens, Map<String, Double> customVariables) { }

    @Override
    public String toString() {
        return name + " = " + expression;
    }
}
