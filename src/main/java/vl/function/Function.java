package vl.function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vl.table.ValueTable;
import vl.token.tokens.ArgumentToken;

@AllArgsConstructor
@Getter
public abstract class Function<X, Y> {
    private final String name;

    public abstract double apply(ArgumentToken<Function<X, Y>> token, ValueTable<X, Y> table, Coordinates<X, Y> coordinates);
}
