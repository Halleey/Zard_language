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
    public final boolean newline;

    public PrintNode(ASTNode expr, boolean newline) {
        this.expr = expr;
        this.newline = newline;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue val = expr.evaluate(ctx);

        if (newline) {
            runtimePrintln(val, ctx);
        } else {
            runtimePrint(val, ctx);
        }

        return val;
    }

    private void runtimePrint(TypedValue val, RuntimeContext ctx) {
        switch (val.type()) {
            case "List" -> {
                DynamicList list = (DynamicList) val.value();
                List<Object> values = list.getElements().stream()
                        .map(node -> node.evaluate(ctx).value())
                        .toList();
                System.out.print(values);
            }
            case "Map" -> {
                printMap(val, ctx, false);
            }
            default -> System.out.print(val.value());
        }
    }

    private void runtimePrintln(TypedValue val, RuntimeContext ctx) {
        switch (val.type()) {
            case "List" -> {
                DynamicList list = (DynamicList) val.value();
                List<Object> values = list.getElements().stream()
                        .map(node -> node.evaluate(ctx).value())
                        .toList();
                System.out.println(values);
            }
            case "Map" -> {
                printMap(val, ctx, true);
            }
            default -> System.out.println(val.value());
        }
    }

    private void printMap(TypedValue val, RuntimeContext ctx, boolean newline) {
        DynamicMap map = (DynamicMap) val.value();
        Map<TypedValue, TypedValue> evaluated = map.evaluate(ctx);

        if (evaluated.isEmpty()) {
            if (newline) System.out.println("{}");
            else System.out.print("{}");
            return;
        }

        System.out.print("{");
        int i = 0;
        int size = evaluated.size();
        for (Map.Entry<TypedValue, TypedValue> e : evaluated.entrySet()) {
            TypedValue key = e.getKey();
            TypedValue value = e.getValue();

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

        if (newline) System.out.println("}");
        else System.out.print("}");
    }


    @Override
    public List<ASTNode> getChildren() {
        return expr != null ? List.of(expr) : List.of();
    }


    @Override
    public void print(String prefix) {
        System.out.println(prefix + (newline ? "PrintLn:" : "Print:"));
        expr.print(prefix + "  ");
    }
}
