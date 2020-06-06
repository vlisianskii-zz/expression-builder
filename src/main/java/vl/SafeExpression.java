package vl;

import vl.algorithms.TokenAlgorithm;
import vl.exception.NotEnoughDataException;
import vl.function.Function;
import vl.table.Result;
import vl.table.ValueTable;

public class SafeExpression extends AbstractExpression<Integer, String> {
    public SafeExpression(String name, String expression, TokenAlgorithm<Integer, String> algorithm, Function<Integer, String>[] functions) {
        super(name, expression, algorithm, functions);
    }

    @Override
    public Result<Integer, String> calculate(ValueTable<Integer, String> table, Integer x) {
        try {
            return super.calculate(table, x);
        } catch (NotEnoughDataException e) {
            String y = (String) e.getY();
            Double value = table.getValue(x, y);
            return Result.<Integer, String>builder()
                    .x(x)
                    .y(y)
                    .value(value)
                    .name(getName())
                    .build();
        }
    }
}
