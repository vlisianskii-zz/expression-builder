package vl.function;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Coordinates<X, Y> {
    private final X x;
    private final Y y;
}
