package vl.algorithms;

import vl.exception.NotEnoughDataException;
import vl.table.ValueTable;
import vl.token.Token;

import java.util.List;
import java.util.Stack;

public class SafeTraverse extends SimpleTraverse {
    @Override
    public Stack<Double> traverse(List<Token<Object>> tokens, ValueTable<Integer, String> table, Integer x, String y) {
        try {
            return super.traverse(tokens, table, x, y);
        } catch (NotEnoughDataException e) {
            Double value = table.getValue(x, (String) e.getY());
            Stack<Double> stack = new Stack<>();
            stack.push(value);
            return stack;
        }
    }
}
