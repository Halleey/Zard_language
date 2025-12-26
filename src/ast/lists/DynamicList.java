package ast.lists;

import ast.ASTNode;
import ast.context.RuntimeContext;
import ast.expressions.TypedValue;
import ast.variables.LiteralNode;

import java.util.ArrayList;
import java.util.List;

public class DynamicList {
    private final List<ASTNode> elements;
    private  String elementType;

    public DynamicList(String elementType, List<ASTNode> elements) {
        this.elementType = elementType;
        this.elements = elements;
    }

    public String getElementType() {
        return elementType;
    }

    public List<ASTNode> getElements() {
        return elements;
    }

    // Avalia todos os elementos no runtime
    public List<TypedValue> evaluate(RuntimeContext ctx) {
        List<TypedValue> result = new ArrayList<>();
        for (ASTNode node : elements) {
            result.add(node.evaluate(ctx));
        }
        return result;
    }

    public TypedValue get(int index, RuntimeContext ctx) {
        return elements.get(index).evaluate(ctx);
    }

    public TypedValue removeByIndex(int index, RuntimeContext ctx) {
        ASTNode removedNode = elements.remove(index);
        return removedNode.evaluate(ctx);
    }
    public void add(TypedValue value) {
        String valType = value.type();

        if (elementType.equals("?") || elementType.equals("any")) {
            if (elements.isEmpty()) {
                // trava o tipo dessa lista na primeira inserção
                this.elementType = valType;
            } else if (!valType.equals(this.elementType)) {
                throw new RuntimeException(
                        "Tipo inválido para lista <" + elementType + ">: " + valType
                );
            }

            elements.add(new LiteralNode(value));
            return;
        }

        boolean ok = valType.equals(elementType);

        if (!ok && valType.startsWith("Struct<")) {
            String inner = valType.substring("Struct<".length(), valType.length() - 1);
            if (inner.equals(elementType)) {
                ok = true;
            }
        }

        if (!ok) {
            throw new RuntimeException("Tipo inválido para lista <" + elementType + ">: " + valType);
        }

        elements.add(new LiteralNode(value));
    }

    public int size() {
        return elements.size();
    }

    @Override
    public String toString() {
        List<TypedValue> vals = evaluate(new RuntimeContext()); // opcional para debug
        return vals.toString();
    }


}
