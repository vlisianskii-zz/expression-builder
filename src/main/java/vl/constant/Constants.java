package vl.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Constants {
    MONTHS_COUNT(12.0),
    PI(3.14159265);

    private final double value;
}
