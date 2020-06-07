package vl;

import org.junit.Before;
import org.junit.Test;
import vl.algorithms.ShuntingYard;
import vl.algorithms.TokenAlgorithm;
import vl.exception.InvalidExpressionException;
import vl.function.Function;
import vl.function.functions.PriorFunction;
import vl.table.Result;
import vl.table.SimpleTable;
import vl.table.ValueTable;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class TrickyExpressionTest {
    private static final String EXPRESSION_NAME = "tricky expression";
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
        Function<Integer, String>[] functions = new Function[]{new PriorFunction()};
        TrickyExpression expression = new TrickyExpression(EXPRESSION_NAME, "self/prior(self)", algorithm, functions);
        List<Result<Integer, String>> results = newArrayList();
        // action
        table.traverse((x, y) -> {
            Result<Integer, String> result = expression.calculate(table, x, y);
            results.add(result);
        });
        // verify
        assertThat(results).containsExactly(
                Result.<Integer, String>builder()
                        .x(2020)
                        .y("A")
                        .value(3.0)
                        .name(EXPRESSION_NAME)
                        .build(),
                Result.<Integer, String>builder()
                        .x(2020)
                        .value(4.0)
                        .y("B")
                        .name(EXPRESSION_NAME)
                        .build(),
                Result.<Integer, String>builder()
                        .x(2021)
                        .y("A")
                        .name(EXPRESSION_NAME)
                        .value(0.3333333333333333)
                        .build(),
                Result.<Integer, String>builder()
                        .x(2021)
                        .y("B")
                        .name(EXPRESSION_NAME)
                        .value(0.55)
                        .build()
        );
    }

    @Test(expected = InvalidExpressionException.class)
    public void throw_exception_when_there_are_other_variables() {
        // setup
        TrickyExpression expression = new TrickyExpression(EXPRESSION_NAME, "self * A", algorithm);
        // action
        expression.calculate();
    }

    @Test(expected = InvalidExpressionException.class)
    public void throw_exception_when_there_are_wrong_operator_arguments() {
        // setup
        TrickyExpression expression = new TrickyExpression(EXPRESSION_NAME, "self *", algorithm);
        // action
        table.traverse((x, y) -> {
            expression.calculate(table, x, y);
        });
    }
}
