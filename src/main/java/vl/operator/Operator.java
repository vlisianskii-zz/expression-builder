package vl.operator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public abstract class Operator {
    public static final int PRECEDENCE_ADDITION = 500;
    public static final int PRECEDENCE_SUBTRACTION = PRECEDENCE_ADDITION;
    public static final int PRECEDENCE_MULTIPLICATION = 1000;
    public static final int PRECEDENCE_DIVISION = PRECEDENCE_MULTIPLICATION;
    public static final int PRECEDENCE_MODULO = PRECEDENCE_DIVISION;
    public static final int PRECEDENCE_POWER = 10000;
    public static final int PRECEDENCE_UNARY_MINUS = 5000;
    public static final int PRECEDENCE_UNARY_PLUS = PRECEDENCE_UNARY_MINUS;

    private final String symbol;
    private final int numOperands;
    private final boolean leftAssociative;
    private final int precedence;

    public abstract double apply(double... args);
}
