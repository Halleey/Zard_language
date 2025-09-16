package variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

import java.util.Map;

public class LiteralNode extends ASTNode {
    public final TypedValue value;

    public LiteralNode(TypedValue value) {
        this.value = value;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return value;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Literal: (" + value.getType() + ") " + value.getValue());
    }

}

