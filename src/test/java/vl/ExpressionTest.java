package vl;

import org.junit.Test;
import vl.algorithms.*;
import vl.exception.NotEnoughDataException;
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

        ValueTable<Integer, String> table = new SimpleTable();
        table.addValue(2000, "A", 3.0);
        table.addValue(2000, "B", 4.0);
        table.addValue(2000, "C", 5.0);

        table.addValue(2001, "A", 1.0);
        table.addValue(2001, "B", 2.0);
        table.addValue(2001, "C", 2.5);

        AbstractExpression<Integer, String> simpleExpression = new Expression("AB", "A + next(B)", algorithm, functions);
        System.out.println("Traverse each cell: " + simpleExpression);
        table.traverseEach((x, y) -> {
            Result<Integer, String> result = simpleExpression.calculate(table, x, y);
            if (!result.isEmpty()) {
                System.out.println(result);
            }
        });

        AbstractExpression<Integer, String> safeExpression = new SafeExpression("SAFE", "next(C) * C", algorithm, functions);
        System.out.println("Traverse by x: " + safeExpression);
        table.traverse((x) -> {
            Result<Integer, String> result = safeExpression.calculate(table, x);
            if (!result.isEmpty()) {
                System.out.println(result);
            }
        });


    }
}
