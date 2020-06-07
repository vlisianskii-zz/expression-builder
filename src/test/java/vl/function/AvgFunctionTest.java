package vl.function;

import org.junit.Before;
import org.junit.Test;
import vl.exception.InvalidTokenException;
import vl.exception.NotEnoughDataException;
import vl.table.SimpleTable;
import vl.table.ValueTable;
import vl.token.TokenType;
import vl.token.tokens.ArgumentToken;

import static org.assertj.core.api.Assertions.assertThat;

public class AvgFunctionTest {
    private Function<Integer, String> function;
    private ArgumentToken<Function<Integer, String>> token;
    private ValueTable<Integer, String> table;

    @Before
    public void before() {
        function = new AvgFunction();
        token = new ArgumentToken<>(TokenType.FUNCTION, function, "A,2");
        table = new SimpleTable();
        table.addValue(2000, "A", 3.0);
        table.addValue(2001, "A", 1.0);
    }

    @Test
    public void return_next_value_by_x() {
        // setup
        Coordinates<Integer, String> coordinates = Coordinates.<Integer, String>builder()
                .x(2000)
                .y("A")
                .build();
        // action
        double response = function.apply(token, table, coordinates);
        // verify
        assertThat(response).isEqualTo(2.0);
    }

    @Test(expected = NotEnoughDataException.class)
    public void throw_exception_when_no_next_value_by_x() {
        // setup
        Coordinates<Integer, String> coordinates = Coordinates.<Integer, String>builder()
                .x(2001)
                .y("A")
                .build();
        // action & verify
        function.apply(token, table, coordinates);
    }

    @Test(expected = InvalidTokenException.class)
    public void throw_exception_when_second_argument_is_not_number() {
        // setup
        token = new ArgumentToken<>(TokenType.FUNCTION, function, "A,B");
        Coordinates<Integer, String> coordinates = Coordinates.<Integer, String>builder()
                .x(2001)
                .y("A")
                .build();
        // action & verify
        function.apply(token, table, coordinates);
    }

    @Test(expected = InvalidTokenException.class)
    public void throw_exception_when_more_than_max_arguments() {
        // setup
        token = new ArgumentToken<>(TokenType.FUNCTION, function, "A,2,3");
        Coordinates<Integer, String> coordinates = Coordinates.<Integer, String>builder()
                .x(2001)
                .y("A")
                .build();
        // action & verify
        function.apply(token, table, coordinates);
    }
}
