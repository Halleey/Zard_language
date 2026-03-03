package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.variables.LiteralNode;

import java.util.ArrayList;
import java.util.List;
public final class DynamicList {

    private final List<ASTNode> elements;
    private String elementType; // "?" até o bind()
    private final boolean isReference;

    public DynamicList(String elementType, List<ASTNode> elements, boolean isReference) {
        this.elementType = elementType;
        this.elements = elements;
        this.isReference = isReference;
    }

    public List<ASTNode> getElements() {
        return elements;
    }

    public String getElementType() {
        return elementType;
    }

    public void lockElementType(String type) {
        this.elementType = type;
    }

    public boolean isReference() {
        return isReference;
    }
}