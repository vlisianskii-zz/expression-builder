package vl.algorithms;

import vl.function.Function;
import vl.token.Token;

import java.util.List;

public interface TokenAlgorithm<X, Y> {
    List<Token<Object>> tokenize(String expression, Function<X, Y>[] functions);
}
