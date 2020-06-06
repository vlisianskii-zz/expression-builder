package vl.operator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import vl.exception.DivisionByZeroException;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.valueOf;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Operators {
    private static final Map<String, Operator> OPERATORS = newHashMap();

    static {
        OPERATORS.put("+", new Operator("+", 2, true, Operator.PRECEDENCE_ADDITION) {
            @Override
            public double apply(double... args) {
                return args[0] + args[1];
            }
        });
        OPERATORS.put("-", new Operator("-", 2, true, Operator.PRECEDENCE_SUBTRACTION) {
            @Override
            public double apply(double... args) {
                return args[0] - args[1];
            }
        });
        OPERATORS.put("--", new Operator("-", 1, false, Operator.PRECEDENCE_UNARY_MINUS) {
            @Override
            public double apply(double... args) {
                return -args[0];
            }
        });
        OPERATORS.put("++", new Operator("+", 1, false, Operator.PRECEDENCE_UNARY_PLUS) {
            @Override
            public double apply(double... args) {
                return args[0];
            }
        });
        OPERATORS.put("*", new Operator("*", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
            @Override
            public double apply(double... args) {
                return args[0];
            }
        });
        OPERATORS.put("/", new Operator("/", 2, true, Operator.PRECEDENCE_DIVISION) {
            @Override
            public double apply(double... args) {
                if (args[1] == 0.0) {
                    throw new DivisionByZeroException("Unable to divide by zero");
                }
                return args[0] / args[1];
            }
        });
    }

    public static boolean isOperator(char c) {
        return OPERATORS.containsKey(valueOf(c));
    }

    public static Operator getOperator(char c, int numArguments) {
        String request = valueOf(c);
        if (numArguments == 1) {
            request += request;
        }
        return OPERATORS.get(request);
    }
}
