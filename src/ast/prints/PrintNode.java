package ast.prints;

import ast.ASTNode;
import ast.maps.DynamicMap;
import ast.runtime.RuntimeContext;
import ast.lists.DynamicList;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;


public class PrintNode extends ASTNode {
    public final ASTNode expr;

    public PrintNode(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue val = expr.evaluate(ctx);

        switch (val.getType()) {
            case "List" -> {
                DynamicList list = (DynamicList) val.getValue();
                // Avalia cada elemento do DynamicList
                List<Object> values = list.getElements().stream()
                        .map(node -> node.evaluate(ctx).getValue())
                        .toList();
                System.out.println(values);
            }
            case "Map" -> {
                DynamicMap map = (DynamicMap) val.getValue();
                if (map.size() == 0) {
                    System.out.println("{}");
                } else {
                    System.out.print("{");
                    int i = 0;
                    for (TypedValue keyVal : map.keys()) {
                        TypedValue valueVal = map.get(keyVal);
                        System.out.print(keyVal.getValue() + ": " + valueVal.getValue());
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