package vl.algorithms;

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
import java.util.Stack;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

public class SimpleTraverse implements TokenTraverse<Integer, String> {
    private final Map<String, Double> constants;

    public SimpleTraverse() {
        this.constants = stream(Constants.values())
                .collect(Collectors.toMap(Constants::name, Constants::getValue));
    }

    @Override
    public Stack<Double> traverse(List<Token<Object>> tokens, ValueTable<Integer, String> table, Integer x, String y) {
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
        return output;
    }

    private Double applyVariable(Token<Object> token, ValueTable<Integer, String> table, Integer x, String y) {
        String name = (String) token.getValue();
        if (constants.containsKey(name)) {
            return constants.get(name);
        }

        if (isNull(y)) {
            return table.getValue(x, name);
        }
        return table.getValue(x, y);
    }

    private Double applyFunction(Function<Integer, String> function, Token<Object> token, ValueTable<Integer, String> table, Integer x, String y) {
        Coordinates<Integer, String> coordinates = Coordinates.<Integer, String>builder()
                .x(x)
                .y(isNull(y) ? token.getArguments() : y)
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
}
