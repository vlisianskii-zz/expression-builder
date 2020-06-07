package vl;

import org.junit.Before;
import org.junit.Test;
import vl.algorithms.ShuntingYard;
import vl.algorithms.TokenAlgorithm;
import vl.function.Function;
import vl.function.PriorFunction;
import vl.table.Result;
import vl.table.SimpleTable;
import vl.table.ValueTable;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class TrickyExpressionTest {
    private TokenAlgorithm<Integer, String> algorithm;
    private ValueTable<Integer, String> table;

    @Before
    public void before() {
        algorithm = new ShuntingYard<>();

        table = new SimpleTable();
        table.addValue(2020, "A", 3.0); //     2020  2021
        table.addValue(2020, "B", 4.0); //  A  3.0   1.0
        table.addValue(2021, "A", 1.0); //  B  4.0   2.0
        table.addValue(2021, "B", 2.2);
    }

    @Test
    public void return_single_result_for_tricky_expression() {
        // setup
        String expressionName = "tricky expression";
        Function<Integer, String>[] functions = new Function[]{new PriorFunction()};
        TrickyExpression expression = new TrickyExpression(expressionName, "prior(self)/self", algorithm, functions);
        List<Result<Integer, String>> results = newArrayList();
        // action
        table.traverse((x, y) -> {
            Result<Integer, String> result = expression.calculate(table, x, y);
            System.out.println(result);
            results.add(result);
        });
        // verify
        assertThat(results).containsExactly(
                Result.<Integer, String>builder()
                        .x(2020)
                        .y("A")
                        .value(3.0)
                        .name(expressionName)
                        .build(),
                Result.<Integer, String>builder()
                        .x(2020)
                        .value(4.0)
                        .y("B")
                        .name(expressionName)
                        .build(),
                Result.<Integer, String>builder()
                        .x(2021)
                        .y("A")
                        .name(expressionName)
                        .value(3.0)
                        .build(),
                Result.<Integer, String>builder()
                        .x(2021)
                        .y("B")
                        .name(expressionName)
                        .value(1.8181818181818181)
                        .build()
        );
    }
}
