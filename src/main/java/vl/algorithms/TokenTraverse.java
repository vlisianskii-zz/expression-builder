package vl.algorithms;

import vl.table.ValueTable;
import vl.token.Token;

import java.util.List;
import java.util.Stack;

public interface TokenTraverse<X, Y> {
    Stack<Double> traverse(List<Token<Object>> tokens, ValueTable<X, Y> table, X x, Y y);
}
