package vl;

import org.junit.Test;
import vl.algorithms.TokenAlgorithm;
import vl.table.SimpleTable;
import vl.table.ValueTable;
import vl.token.ShuntingYard;

public class ExpressionTest {
    @Test
    public void test() {
        TokenAlgorithm<Integer, String> algorithm = new ShuntingYard<>();
        Expression<Integer, String> expression = new Expression<>("AB", "A + B / (-C)", algorithm);

        ValueTable<Integer, String> table = new SimpleTable();
        table.addValue(2000, "A", 3.0);
        table.addValue(2000, "B", 4.0);
        table.addValue(2000, "C", 5.0);

        table.addValue(2001, "A", 1.0);
        table.addValue(2001, "B", 2.0);
        table.addValue(2001, "C", 2.5);

        System.out.println("Traverse by x");
        table.traverse((x) -> {
            Double value = expression.calculate(table, x);
            System.out.println(String.format("%s = %s", x, value));
        });

        System.out.println("Traverse each cell");
        table.traverseEach((x, y) -> {
            Double value = expression.calculate(table, x, y);
            System.out.println(String.format("%s + %s = %s", x, y, value));
        });
    }
}
