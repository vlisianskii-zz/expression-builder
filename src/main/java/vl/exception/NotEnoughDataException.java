package vl.exception;

import lombok.Getter;

@Getter
public class NotEnoughDataException extends RuntimeException {
    private final Object y;

    public NotEnoughDataException(Object x, Object y) {
        super(String.format("Not enough input data by x='%s' y='%s'", x, y));
        this.y = y;
    }
}
