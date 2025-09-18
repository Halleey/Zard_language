package prints;

import ast.ASTNode;
import ast.maps.DynamicMap;
import ast.runtime.RuntimeContext;
import ast.lists.DynamicList;
import expressions.TypedValue;

import java.util.List;
public class PrintNode extends ASTNode {
    final ASTNode expr;

    public PrintNode(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue val = expr.evaluate(ctx);

        switch (val.getType()) {
            case "list" -> {
                DynamicList list = (DynamicList) val.getValue();
                // Avalia cada elemento do DynamicList
                List<Object> values = list.getElements().stream()
                        .map(node -> node.evaluate(ctx).getValue())
                        .toList();
                System.out.println(values);
            }
            case "map" -> {
                DynamicMap map = (DynamicMap) val.getValue();
                if (map.size() == 0) {
                    System.out.println("{}");
                } else {
                    System.out.print("{");
                    int i = 0;
                    ASTNode[] valueNodes = map.valueNodes().toArray(new ASTNode[0]);
                    for (ASTNode keyNode : map.keyNodes()) {
                        ASTNode valueNode = valueNodes[i];
                        Object keyVal = keyNode.evaluate(ctx).getValue();
                        Object valueVal = valueNode.evaluate(ctx).getValue();
                        System.out.print(keyVal + ": " + valueVal);
                        if (i < map.size() - 1) System.out.print(", ");
                        i++;
                    }
                    System.out.println("}");
                }
            }
            default -> System.out.println(val.getValue());
        }

        return val;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Print:");
        expr.print(prefix + "  ");
    }
}
