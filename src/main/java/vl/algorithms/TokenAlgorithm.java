package vl.algorithms;

import vl.function.Function;
import vl.token.tokens.ExpressionToken;

import java.util.List;

public interface TokenAlgorithm<X, Y> {
    List<ExpressionToken> tokenize(String expression, Function<X, Y>[] functions);
}
