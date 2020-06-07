package vl.table;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface AbstractTable<X, Y, V> {
    void addValue(X x, Y y, V v);
    V getValue(X x, Y y);
    void traverse(BiConsumer<X, Y> consumer);
    void traverse(Consumer<X> consumer);
}
