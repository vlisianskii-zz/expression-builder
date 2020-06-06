package vl;

import vl.algorithms.TokenAlgorithm;
import vl.exception.NotEnoughDataException;
import vl.function.Coordinates;
import vl.function.Function;
import vl.table.Result;
import vl.table.ValueTable;

import java.util.Collections;
import java.util.Map;

public class SimpleExpression extends AbstractExpression<Integer, String> {
    public SimpleExpression(String name, String expression, TokenAlgorithm<Integer, String> algorithm) {
        super(name, expression, algorithm, null);
    }

    public SimpleExpression(String name, String expression, TokenAlgorithm<Integer, String> algorithm, Function<Integer, String>[] functions) {
        super(name, expression, algorithm, functions);
    }

    public Result<Integer, String> calculate(ValueTable<Integer, String> table, Integer x) {
        return calculate(table, x, Collections.emptyMap());
    }

    public Result<Integer, String> calculate(ValueTable<Integer, String> table, Integer x, Map<String, Double> customVariable) {
        try {
            return super.compute(table, x, null, customVariable);
        } catch (NotEnoughDataException ignore) {
            return Result.<Integer, String>builder()
                    .x(x)
                    .name(getName())
                    .build();
        }
    }

    @Override
    protected Double getValueFromTable(ValueTable<Integer, String> table, Integer x, String y, String tokenName) {
        return table.getValue(x, tokenName);
    }

    @Override
    protected Coordinates<Integer, String> getCoordinates(Integer x, String y, String arguments) {
        return Coordinates.<Integer, String>builder()
                .x(x)
                .y(arguments)
                .build();
    }
}
