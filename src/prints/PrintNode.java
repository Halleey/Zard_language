package prints;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.Map;

public class PrintNode extends ASTNode {
    final ASTNode expr;

    public PrintNode(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        TypedValue val = expr.evaluate(variables);
        System.out.println(val.getValue());
        return val;
    }
}