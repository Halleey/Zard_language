package ast.exceptions;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

import java.util.Map;

public class BreakNode extends ASTNode {
    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        throw new BreakLoop(); // exceção para controlar break
    }
}

