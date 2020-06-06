package vl.function;

import vl.table.ValueTable;
import vl.token.Token;

public class NextFunction extends Function<Integer, String> {
    public NextFunction() {
        super("next");
    }

    @Override
    public double apply(Token<Object> token, ValueTable<Integer, String> table, Coordinates<Integer, String> coordinates) {
        return table.getValue(coordinates.getX() + 1, coordinates.getY());
    }
}
