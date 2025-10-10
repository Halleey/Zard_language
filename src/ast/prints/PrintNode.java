package ast.prints;

import ast.ASTNode;
import ast.maps.DynamicMap;
import ast.runtime.RuntimeContext;
import ast.lists.DynamicList;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;
import java.util.Map;


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
                List<Object> values = list.getElements().stream()
                        .map(node -> node.evaluate(ctx).getValue())
                        .toList();
                System.out.println(values);
            }

            case "Map" -> {
                DynamicMap map = (DynamicMap) val.getValue();
                Map<TypedValue, TypedValue> evaluated = map.evaluate(ctx);

                if (evaluated.isEmpty()) {
                    System.out.println("{}");
                } else {
                    System.out.print("{");
                    int i = 0;
                    int size = evaluated.size();
                    for (Map.Entry<TypedValue, TypedValue> e : evaluated.entrySet()) {
                        TypedValue key = e.getKey();
                        TypedValue value = e.getValue();

                        // Formatação segura para strings
                        String keyStr = key.getType().equals("string")
                                ? "\"" + key.getValue() + "\""
                                : String.valueOf(key.getValue());

                        String valStr = value.getType().equals("string")
                                ? "\"" + value.getValue() + "\""
                                : String.valueOf(value.getValue());

                        System.out.print(keyStr + ": " + valStr);
                        if (i < size - 1) System.out.print(", ");
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
