package vl.function.functions;

import vl.function.Coordinates;
import vl.function.Function;
import vl.table.ValueTable;
import vl.token.tokens.ArgumentToken;

public class NextFunction extends Function<Integer, String> {
    public NextFunction() {
        super("next");
    }

    @Override
    public double apply(ArgumentToken<Function<Integer, String>> token, ValueTable<Integer, String> table, Coordinates<Integer, String> coordinates) {
        return table.getValue(coordinates.getX() + 1, coordinates.getY());
    }
}
