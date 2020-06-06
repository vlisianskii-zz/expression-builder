package vl.table;

public interface ValueTable<X, Y> extends AbstractTable<X, Y, Double> {
    void addValue(X x, Y y, Double v);

    Double getValue(X x, Y y);
}
