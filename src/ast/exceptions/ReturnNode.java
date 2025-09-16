package ast.exceptions;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.Map;

public class ReturnNode extends ASTNode {

    public final ASTNode expr;

    public ReturnNode(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        TypedValue value = expr.evaluate(variables);
        throw new ReturnValue(value);
    }
}
