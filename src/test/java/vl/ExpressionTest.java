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
        String name = "AB";
        String expression = "A + next(B)";

        ValueTable<Integer, String> table = new SimpleTable();
        table.addValue(2000, "A", 3.0);
        table.addValue(2000, "B", 4.0);
        table.addValue(2000, "C", 5.0);

        table.addValue(2001, "A", 1.0);
        table.addValue(2001, "B", 2.0);
        table.addValue(2001, "C", 2.5);


        AbstractExpression<Integer, String> e1 = new SafeExpression(name, expression, algorithm, functions);
        System.out.println("Traverse by x");
        table.traverse((x) -> {
            Result<Integer, String> result = e1.calculate(table, x);
            System.out.println(result);
        });

        AbstractExpression<Integer, String> e2 = new Expression(name, expression, algorithm, functions);
        System.out.println("Traverse each cell");
        table.traverseEach((x, y) -> {
            try {
                Result<Integer, String> result = e2.calculate(table, x, y);
                System.out.println(result);
            } catch (NotEnoughDataException ignore) {}
        });
    }
}
