package vl;

import vl.algorithms.TokenTraverse;
import vl.table.Result;
import vl.table.ValueTable;

import java.util.Objects;

public abstract class AbstractExpression<X, Y> {
    private final String name;
    private final String expression;

    public AbstractExpression(String name, String expression) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(expression);

        this.name = name;
        this.expression = expression;
    }

    public Result<X, Y> calculate(TokenTraverse<X, Y> traverse, ValueTable<X, Y> table, X x) {
        return calculate(traverse, table, x, null);
    }

    public abstract Result<X, Y> calculate(TokenTraverse<X, Y> traverse, ValueTable<X, Y> table, X x, Y y);

    @Override
    public String toString() {
        return name + " = " + expression;
    }
}
