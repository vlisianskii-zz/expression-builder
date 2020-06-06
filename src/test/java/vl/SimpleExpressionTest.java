package vl;

import org.junit.Test;
import vl.algorithms.*;
import vl.function.Function;
import vl.function.NextFunction;
import vl.function.PriorFunction;
import vl.table.Result;
import vl.table.SimpleTable;
import vl.table.ValueTable;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class SimpleExpressionTest {
    @Test
    public void test() {
        Function<Integer, String>[] functions = new Function[]{new NextFunction(), new PriorFunction()};
        TokenAlgorithm<Integer, String> algorithm = new ShuntingYard<>();

        ValueTable<Integer, String> table = new SimpleTable();
        table.addValue(2000, "A", 3.0);
        table.addValue(2000, "B", 4.0);
        table.addValue(2000, "C", 5.0);

        table.addValue(2001, "A", 1.0);
        table.addValue(2001, "B", 2.0);
        table.addValue(2001, "C", 2.5);

        SimpleExpression simpleExpression = new SimpleExpression("AB", "A + next(B)", algorithm, functions);
        System.out.println("Traverse each cell: " + simpleExpression);
        table.traverseEach((x, y) -> {
            Result<Integer, String> result = simpleExpression.calculate(table, x, y);
            if (!result.isEmpty()) {
                System.out.println(result);
            }
        });

        Map<String, Double> customVariable = newHashMap();
        customVariable.put("X", 30.1);

        TrickyExpression trickyExpression = new TrickyExpression("COMPLEX", "X / (next(C) * C)", algorithm, functions);
        System.out.println("Traverse by x: " + trickyExpression);
        table.traverse((x) -> {
            Result<Integer, String> result = trickyExpression.calculate(table, x, customVariable);
            if (!result.isEmpty()) {
                System.out.println(result);
            }
        });


    }
}
