package vl.function.functions;

import vl.function.Coordinates;
import vl.function.Function;
import vl.table.ValueTable;
import vl.token.tokens.ArgumentToken;

public class PriorFunction extends Function<Integer, String> {
    public PriorFunction() {
        super("prior");
    }

    @Override
    public double apply(ArgumentToken<Function<Integer, String>> token, ValueTable<Integer, String> table, Coordinates<Integer, String> coordinates) {
        return table.getValue(coordinates.getX() - 1, coordinates.getY());
    }
}
