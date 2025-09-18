package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import variables.LiteralNode;

import java.util.ArrayList;
import java.util.List;

public class DynamicList {
    private final List<ASTNode> elements;

    public DynamicList(List<ASTNode> elements) {
        this.elements = elements;
    }

    // Avaliação no runtime
    public List<TypedValue> evaluate(RuntimeContext ctx) {
        List<TypedValue> result = new ArrayList<>();
        for (ASTNode node : elements) {
            result.add(node.evaluate(ctx));
        }
        return result;
    }
    public String toString() {
        List<TypedValue> vals = evaluate(new RuntimeContext()); // opcional para debug
        return vals.toString();
    }

    public List<ASTNode> getElements() {
        return elements;
    }
    // Avalia e retorna elemento específico
    public TypedValue get(int index, RuntimeContext ctx) {
        return elements.get(index).evaluate(ctx);
    }


    public TypedValue removeByIndex(int index, RuntimeContext ctx) {
        ASTNode removedNode = elements.remove(index);
        return removedNode.evaluate(ctx);
    }



    public void add(TypedValue value) {
        elements.add(new LiteralNode(value));
    }

    public void clear() {
        elements.clear();
    }

    public int size() {
        return elements.size();
    }



}
