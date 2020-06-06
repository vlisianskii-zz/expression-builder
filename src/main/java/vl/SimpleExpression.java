package vl;

import vl.algorithms.TokenAlgorithm;
import vl.exception.NotEnoughDataException;
import vl.function.Function;
import vl.table.Result;
import vl.table.ValueTable;

import java.util.Collections;

public class SimpleExpression extends AbstractExpression<Integer, String> {
    public SimpleExpression(String name, String expression, TokenAlgorithm<Integer, String> algorithm, Function<Integer, String>[] functions) {
        super(name, expression, algorithm, functions);
    }

    public Result<Integer, String> calculate(ValueTable<Integer, String> table, Integer x, String y) {
        try {
            return super.compute(table, x, y, Collections.emptyMap());
        } catch (NotEnoughDataException ignore) {
            return Result.<Integer, String>builder()
                    .x(x)
                    .y(y)
                    .name(getName())
                    .build();
        }
    }
}
