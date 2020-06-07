package vl.function.functions;

import vl.exception.InvalidFunctionArguments;
import vl.exception.InvalidTokenException;
import vl.function.Coordinates;
import vl.function.Function;
import vl.table.ValueTable;
import vl.token.tokens.ArgumentToken;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class AvgFunction extends Function<Integer, String> {
    private static final Integer AVG_ARGUMENTS_COUNT = 2;

    public AvgFunction() {
        super("avg");
    }

    @Override
    public double apply(ArgumentToken<Function<Integer, String>> token, ValueTable<Integer, String> table, Coordinates<Integer, String> coordinates) {
        String arguments = token.getArguments();
        int count = convert(arguments);

        List<Double> values = newArrayList();
        int startPoint = coordinates.getX();
        for (int i = startPoint; i < startPoint + count; i++) {
            Double value = table.getValue(i, coordinates.getY());
            values.add(value);
        }
        return values.stream()
                .mapToDouble(v -> v)
                .average()
                .orElseThrow(() -> new InvalidFunctionArguments("Unable to compute average of argument: " + arguments));
    }

    private Integer convert(String arguments) {
        String[] parts = arguments.split(",");
        if (parts.length != AVG_ARGUMENTS_COUNT) {
            throw new InvalidTokenException(String.format("Unable to parse necessary count of arguments '%s' for function [%s]", AVG_ARGUMENTS_COUNT, arguments));
        }
        try {
            return Integer.valueOf(parts[1]);
        } catch (NumberFormatException e) {
            throw new InvalidTokenException(String.format("Unable to parse arguments for function [%s]: %s", arguments, e));
        }
    }
}
