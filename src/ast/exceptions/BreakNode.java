package ast.exceptions;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.Map;

public class BreakNode extends ASTNode {
    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        throw new BreakLoop();
    }
}
