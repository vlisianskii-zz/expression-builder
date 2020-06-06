package vl;

import org.junit.Before;
import org.junit.Test;
import vl.algorithms.*;
import vl.function.Function;
import vl.function.NextFunction;
import vl.table.Result;
import vl.table.SimpleTable;
import vl.table.ValueTable;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleExpressionTest {
    private TokenAlgorithm<Integer, String> algorithm;
    private ValueTable<Integer, String> table;

    @Before
    public void before() {
        algorithm = new ShuntingYard<>();

        table = new SimpleTable();
        table.addValue(2020, "A", 3.0); //     2020  2021
        table.addValue(2020, "B", 4.0); //  A  3.0   1.0
        table.addValue(2021, "A", 1.0); //  B  4.0   2.0
        table.addValue(2021, "B", 2.0);
    }


    @Test
    public void return_single_result_for_numbers() {
        // setup
        String expressionName = "expression with numbers";
        SimpleExpression expression = new SimpleExpression(expressionName, "1+(-2/3)*4", algorithm);
        // action
        Result<Integer, String> result = expression.calculate();
        // verify
        assertThat(result).isEqualTo(Result.<Integer, String>builder()
                .name(expressionName)
                .value(-1.6666666666666665)
                .build()
        );
    }

    @Test
    public void return_single_result_for_simple_table() {
        // setup
        String expressionName = "simple expression";
        SimpleExpression expression = new SimpleExpression(expressionName, "2 / (A + B)", algorithm);
        // action
        Result<Integer, String> result = expression.calculate(table, 2020);
        // verify
        assertThat(result).isEqualTo(Result.<Integer, String>builder()
                .x(2020)
                .name(expressionName)
                .value(0.2857142857142857)
                .build()
        );
    }

    @Test
    public void return_all_x_results_for_simple_table() {
        // setup
        String expressionName = "simple expression";
        SimpleExpression expression = new SimpleExpression(expressionName, "2 / (A + B)", algorithm);
        List<Result<Integer, String>> results = newArrayList();
        // action
        table.traverse((x) -> {
            Result<Integer, String> result = expression.calculate(table, x);
            results.add(result);
        });
        // verify
        assertThat(results).containsExactly(
                Result.<Integer, String>builder()
                        .x(2020)
                        .name(expressionName)
                        .value(0.2857142857142857)
                        .build(),
                Result.<Integer, String>builder()
                        .x(2021)
                        .name(expressionName)
                        .value(0.6666666666666666)
                        .build()
        );
    }

    @Test
    public void return_single_result_for_simple_table_with_custom_variables() {
        // setup
        String expressionName = "expression with custom variables";
        SimpleExpression expression = new SimpleExpression(expressionName, "(X - A)/B * 0.4", algorithm);
        Map<String, Double> customVariable = newHashMap();
        customVariable.put("X", 12.1);
        // action
        Result<Integer, String> result = expression.calculate(table, 2020, customVariable);
        // verify
        assertThat(result).isEqualTo(Result.<Integer, String>builder()
                .x(2020)
                .name(expressionName)
                .value(0.91)
                .build()
        );
    }

    @Test
    public void return_all_x_results_for_simple_table_with_functions() {
        // setup
        String expressionName = "expression with next function";
        Function<Integer, String>[] functions = new Function[]{new NextFunction()};
        SimpleExpression expression = new SimpleExpression(expressionName, "A - next(B)", algorithm, functions);
        List<Result<Integer, String>> results = newArrayList();
        // action
        table.traverse((x) -> {
            Result<Integer, String> result = expression.calculate(table, x);
            System.out.println(result);
            results.add(result);
        });
        // verify
        assertThat(results).containsExactly(
                Result.<Integer, String>builder()
                        .x(2020)
                        .name(expressionName)
                        .value(1.0)
                        .build(),
                Result.<Integer, String>builder()
                        .x(2021)
                        .name(expressionName)
                        .value(null)
                        .build()
        );
    }

    @Test
    public void return_single_result_for_constant_variables() {
        // setup
        String expressionName = "expression with constants";
        SimpleExpression expression = new SimpleExpression(expressionName, "MONTHS_COUNT/PI", algorithm);
        // action
        Result<Integer, String> result = expression.calculate();
        System.out.println(result);
        // verify
        assertThat(result).isEqualTo(Result.<Integer, String>builder()
                .name(expressionName)
                .value(3.819718638570153)
                .build()
        );
    }
}
