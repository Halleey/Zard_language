package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.variables.LiteralNode;

import java.util.ArrayList;
import java.util.List;

public class DynamicList {
    private final List<ASTNode> elements;
    private final String elementType;

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

    // Adiciona um elemento verificando tipo
    public void add(TypedValue value) {
        if (!value.getType().equals(elementType)) {
            throw new RuntimeException("Tipo inválido para lista <" + elementType + ">: " + value.getType());
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
