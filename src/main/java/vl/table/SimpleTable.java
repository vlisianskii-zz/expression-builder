package vl.table;

import vl.exception.InvalidValueException;
import vl.exception.NotEnoughDataException;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Objects.isNull;

public class SimpleTable implements ValueTable<Integer, String> {
    private final Map<Integer, Map<String, Double>> table;

    public SimpleTable() {
        this.table = newHashMap();
    }

    public SimpleTable(Map<Integer, Map<String, Double>> table) {
        this.table = table;
    }

    @Override
    public void traverseEach(BiConsumer<Integer, String> consumer) {
        for (Map.Entry<Integer, Map<String, Double>> x_key: table.entrySet()) {
            for (Map.Entry<String, Double> y_key: x_key.getValue().entrySet()) {
                consumer.accept(x_key.getKey(), y_key.getKey());
            }
        }
    }

    @Override
    public void traverse(Consumer<Integer> consumer) {
        for (Map.Entry<Integer, Map<String, Double>> x_key: table.entrySet()) {
            consumer.accept(x_key.getKey());
        }
    }

    @Override
    public void addValue(Integer x, String y, Double v) {
        table.computeIfAbsent(x, r -> newHashMap()).put(y, v);
    }

    @Override
    public Double getValue(Integer x, String y) {
        if (!table.containsKey(x)) {
            throwNotEnoughData(x, y);
        }
        Map<String, Double> row = table.get(x);
        if (!row.containsKey(y)) {
            throwNotEnoughData(x, y);
        }
        Double value = row.get(y);
        if (isNull(value)) {
            throwNullableData(x, y);
        }
        return value;
    }

    private void throwNullableData(Integer x, String y) {
        throw new InvalidValueException(String.format("Unable to compute NULL table value by x='%s' y='%s'", x, y));
    }

    private void throwNotEnoughData(Integer x, String y) {
        throw new NotEnoughDataException(x, y);
    }
}
