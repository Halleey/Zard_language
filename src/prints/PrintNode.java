package prints;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.DynamicList;
import expressions.TypedValue;

import java.util.Map;
public class PrintNode extends ASTNode {
    final ASTNode expr;

    public PrintNode(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue val = expr.evaluate(ctx);
        if (val.getType().equals("list")) {
            DynamicList list = (DynamicList) val.getValue();
            System.out.println(list.getElements().stream()
                    .map(TypedValue::getValue)
                    .toList());
        }
        else {
            System.out.println(val.getValue());
        }
        return val;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Print:");
        expr.print(prefix + "  ");
    }
}
