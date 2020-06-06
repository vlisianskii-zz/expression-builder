package vl;

import vl.algorithms.TokenAlgorithm;
import vl.algorithms.TokenTraverse;
import vl.exception.InvalidExpressionException;
import vl.function.Function;
import vl.table.Result;
import vl.table.ValueTable;
import vl.token.Token;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static java.util.Objects.isNull;

public class Expression extends AbstractExpression<Integer, String> {
    private final String name;
    private final List<Token<Object>> tokens;

    public Expression(String name, String expression, TokenAlgorithm<Integer, String> algorithm) {
        this(name, expression, algorithm, null);
    }

    public Expression(String name, String expression, TokenAlgorithm<Integer, String> algorithm, Function<Integer, String>[] functions) {
        super(name, expression);
        Objects.requireNonNull(name);
        Objects.requireNonNull(expression);

        this.name = name;
        this.tokens = algorithm.tokenize(expression, functions);
    }

    @Override
    public Result<Integer, String> calculate(TokenTraverse<Integer, String> traverse, ValueTable<Integer, String> table, Integer x, String y) {
        Stack<Double> output = traverse.traverse(tokens, table, x, y);
        Double value = output.pop();
        if (!output.isEmpty()) {
            throw new InvalidExpressionException("Output queue is not empty: " + this);
        }
        return Result.<Integer, String>builder()
                .x(x)
                .y(isNull(y) ? name : y)
                .value(value)
                .build();
    }
}
