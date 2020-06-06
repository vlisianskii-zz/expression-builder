package vl.exception;

public class MismatchParenthesesException extends RuntimeException {
    public MismatchParenthesesException(String expression) {
        super("Mismatch parentheses found: " + expression);
    }
}
