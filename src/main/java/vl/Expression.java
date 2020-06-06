package vl;

import vl.algorithms.TokenAlgorithm;
import vl.constant.Constants;
import vl.exception.InvalidExpressionException;
import vl.exception.InvalidTokenException;
import vl.function.Coordinates;
import vl.function.Function;
import vl.operator.Operator;
import vl.table.ValueTable;
import vl.token.Token;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

@SuppressWarnings("unchecked")
public class Expression<X, Y> {
    private final String name;
    private final String expression;
    private final List<Token<Object>> tokens;
    private final Map<String, Double> constants;

    public Expression(String name, String expression, TokenAlgorithm<X, Y> algorithm) {
        this(name, expression, algorithm, null);
    }

    public Expression(String name, String expression, TokenAlgorithm<X, Y> algorithm, Function<X, Y>[] functions) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(expression);

        this.name = name;
        this.expression = expression;
        this.tokens = algorithm.tokenize(expression, functions);
        this.constants = buildConstants();
    }

    public Double calculate(ValueTable<X, Y> table, X x) {
        return calculate(table, x, null);
    }

    public Double calculate(ValueTable<X, Y> table, X x, Y y) {
        Stack<Double> output = new Stack<>();
        for (Token<Object> token : tokens) {
            switch (token.getTokenType()) {
                case NUMBER:
                    output.push(applyNumber((Double)token.getValue()));
                    break;
                case OPERATOR:
                    output.push(applyOperator((Operator)token.getValue(), output));
                    break;
                case FUNCTION:
                    output.push(applyFunction((Function)token.getValue(), token, table, x, y));
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
        return value;
    }

    private Double applyVariable(Token<Object> token, ValueTable<X, Y> table, X x, Y y) {
        String name = (String) token.getValue();
        if (constants.containsKey(name)) {
            return constants.get(name);
        }

        if (isNull(y)) {
            return table.getValue(x, (Y) name);
        }
        return table.getValue(x, y);
    }

    private Double applyFunction(Function<X, Y> function, Token<Object> token, ValueTable<X, Y> table, X x, Y y) {
        Coordinates<X, Y> coordinates = Coordinates.<X, Y>builder()
                .x(x)
                .y(isNull(y) ? (Y)token.getArguments() : y)
                .build();
        return function.apply(token, table, coordinates);
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

    private Map<String, Double> buildConstants() {
        return stream(Constants.values())
                .collect(Collectors.toMap(Constants::name, Constants::getValue));
    }

    @Override
    public String toString() {
        return name + " = " + expression;
    }
}
