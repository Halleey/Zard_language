package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.variables.LiteralNode;
import context.statics.symbols.Type;
import context.statics.symbols.UnknownType;

import java.util.ArrayList;
import java.util.List;
public final class DynamicList {

    private final List<ASTNode> elements;
    private Type elementType; // agora é Type, não String
    private final boolean isReference;

    public DynamicList(Type elementType, List<ASTNode> elements, boolean isReference) {
        this.elementType = elementType != null ? elementType : UnknownType.UNKNOWN_TYPE;
        this.elements = elements;
        this.isReference = isReference;
    }

    public List<ASTNode> getElements() {
        return elements;
    }

    public Type getElementType() {
        return elementType;
    }

    public void lockElementType(Type type) {
        this.elementType = type;
    }

    public boolean isReference() {
        return isReference;
    }
}