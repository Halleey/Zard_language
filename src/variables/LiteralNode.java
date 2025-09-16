package variables;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.Map;

public class LiteralNode extends ASTNode {
    public final TypedValue value;

    public LiteralNode(TypedValue value) {
        this.value = value;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        return value;
    }
}
