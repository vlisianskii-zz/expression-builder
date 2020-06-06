package vl.function;

import vl.table.ValueTable;
import vl.token.Token;

public class PriorFunction extends Function<Integer, String> {
    public PriorFunction() {
        super("prior");
    }

    @Override
    public double apply(Token<Object> token, ValueTable<Integer, String> table, Coordinates<Integer, String> coordinates) {
        return table.getValue(coordinates.getX() - 1, coordinates.getY());
    }
}
