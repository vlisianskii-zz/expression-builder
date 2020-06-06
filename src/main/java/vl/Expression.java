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
    public Result<Integer, String> calculate(ValueTable<Integer, String> table, Integer x, String y) {
        try {
            return super.calculate(table, x, y);
        } catch (NotEnoughDataException ignore) {
            return Result.empty();
        }
    }
}
