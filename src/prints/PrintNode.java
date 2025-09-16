package prints;

import ast.ASTNode;
import expressions.DynamicList;
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
        if (val.getType().equals("list")) {
            DynamicList list = (DynamicList) val.getValue();
            System.out.println(list.getElements().stream()
                    .map(TypedValue::getValue)
                    .toList());
        }
        else{
                System.out.println(val.getValue());
            }

        return val;
    }
}