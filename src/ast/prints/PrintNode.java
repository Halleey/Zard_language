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

        switch (val.type()) {
            case "List" -> {
                DynamicList list = (DynamicList) val.value();
                List<Object> values = list.getElements().stream()
                        .map(node -> node.evaluate(ctx).value())
                        .toList();
                System.out.println(values);
            }

            case "Map" -> {
                DynamicMap map = (DynamicMap) val.value();
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
                        String keyStr = key.type().equals("string")
                                ? "\"" + key.value() + "\""
                                : String.valueOf(key.value());

                        String valStr = value.type().equals("string")
                                ? "\"" + value.value() + "\""
                                : String.valueOf(value.value());

                        System.out.print(keyStr + ": " + valStr);
                        if (i < size - 1) System.out.print(", ");
                        i++;
                    }
                    System.out.println("}");
                }
            }

            default -> System.out.println(val.value());
        }

        return val;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Print:");
        expr.print(prefix + "  ");
    }
}
