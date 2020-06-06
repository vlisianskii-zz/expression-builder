package vl.function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vl.table.ValueTable;
import vl.token.Token;

@AllArgsConstructor
@Getter
public abstract class Function<X, Y> {
    private final String name;

    public abstract double apply(Token<Object> token, ValueTable<X, Y> table, Coordinates<X, Y> coordinates);
}
