package vl;

import vl.algorithms.TokenAlgorithm;
import vl.exception.NotEnoughDataException;
import vl.function.Function;
import vl.table.Result;
import vl.table.ValueTable;

public class Expression extends AbstractExpression<Integer, String> {
    public Expression(String name, String expression, TokenAlgorithm<Integer, String> algorithm, Function<Integer, String>[] functions) {
        super(name, expression, algorithm, functions);
    }

    @Override
    public Result<Integer, String> compute(ValueTable<Integer, String> table, Integer x, String y, String name) {
        try {
            return super.compute(table, x, y, name);
        } catch (NotEnoughDataException ignore) {
            return Result.<Integer, String>builder()
                    .x(x)
                    .y(y)
                    .name(name)
                    .build();
        }
    }
}
