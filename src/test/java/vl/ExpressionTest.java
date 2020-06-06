package vl;

import org.junit.Test;
import vl.algorithms.*;
import vl.function.Function;
import vl.function.NextFunction;
import vl.table.Result;
import vl.table.SimpleTable;
import vl.table.ValueTable;

public class ExpressionTest {
    @Test
    public void test() {
        Function<Integer, String>[] functions = new Function[]{new NextFunction()};
        TokenAlgorithm<Integer, String> algorithm = new ShuntingYard<>();

        TokenTraverse<Integer, String> simpleTraverse = new SimpleTraverse();
        TokenTraverse<Integer, String> safeTraverse = new SafeTraverse();
        AbstractExpression<Integer, String> expression = new Expression("AB", "A + next(B)", algorithm, functions);

        ValueTable<Integer, String> table = new SimpleTable();
        table.addValue(2000, "A", 3.0);
        table.addValue(2000, "B", 4.0);
        table.addValue(2000, "C", 5.0);

        table.addValue(2001, "A", 1.0);
        table.addValue(2001, "B", 2.0);
        table.addValue(2001, "C", 2.5);

        System.out.println("Traverse by x");
        table.traverse((x) -> {
            Result<Integer, String> result = expression.calculate(safeTraverse, table, x);
            System.out.println(result);
        });

        System.out.println("Traverse each cell");
        table.traverseEach((x, y) -> {
            Result<Integer, String> result = expression.calculate(simpleTraverse, table, x, y);
            System.out.println(result);
        });
    }
}
