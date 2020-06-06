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
import vl.token.Token;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Getter
public abstract class AbstractExpression<X, Y> {
    private final String name;
    private final String expression;
    private final List<Token<Object>> tokens;
    private final Map<String, Double> constants;

    public AbstractExpression(String name, String expression, TokenAlgorithm<Integer, String> algorithm, Function<Integer, String>[] functions) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(expression);

        this.name = name;
        this.expression = expression;
        this.tokens = algorithm.tokenize(expression, functions);
        this.constants = stream(Constants.values())
                .collect(Collectors.toMap(Constants::name, Constants::getValue));
        checkTokens(tokens);
    }

    public Result<X, Y> calculate(ValueTable<X, Y> table, X x) {
        return calculate(table, x, null);
    }

    public Result<X, Y> calculate(ValueTable<X, Y> table, X x, Y y) {
        return compute(table, x, y, name);
    }

    protected Result<X, Y> compute(ValueTable<X, Y> table, X x, Y y, String name) {
        Stack<Double> output = new Stack<>();
        for (Token<Object> token : tokens) {
            switch (token.getTokenType()) {
                case NUMBER:
                    output.push(applyNumber((Double) token.getValue()));
                    break;
                case OPERATOR:
                    output.push(applyOperator((Operator) token.getValue(), output));
                    break;
                case FUNCTION:
                    output.push(applyFunction((Function) token.getValue(), token, table, x, y));
                    break;
                case VARIABLE:
                    output.push(applyVariable(token, table, x, y));
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

    private Double applyVariable(Token<Object> token, ValueTable<X, Y> table, X x, Y y) {
        String tokenName = (String) token.getValue();
        if (constants.containsKey(tokenName)) {
            return constants.get(tokenName);
        }
        return getValueFromTable(table, x, y, tokenName);
    }

    protected Double getValueFromTable(ValueTable<X, Y> table, X x, Y y, String tokenName) {
        return table.getValue(x, y);
    }

    private Double applyFunction(Function<X, Y> function, Token<Object> token, ValueTable<X, Y> table, X x, Y y) {
        Coordinates<X, Y> coordinates = getCoordinates(x, y, token.getArguments());
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

    protected void checkTokens(List<Token<Object>> tokens) { }

    @Override
    public String toString() {
        return name + " = " + expression;
    }
}
