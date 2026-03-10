package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;

import java.util.List;


public class ListAddNode extends ASTNode {

    private final ASTNode listNode;
    private final ASTNode valuesNode;
    private Type elementType; // agora Type, não String

    public ListAddNode(ASTNode listNode, ASTNode valuesNode, Type elementType) {
        this.listNode = listNode;
        this.valuesNode = valuesNode;
        this.elementType = elementType;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue listTyped = listNode.evaluate(ctx);

        if (!(listTyped.value() instanceof ListValue list)) {
            throw new RuntimeException("Target is not a list");
        }

        TypedValue value = valuesNode.evaluate(ctx);
        list.add(value);

        return value;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListAdd (type=" + elementType + "):");
        System.out.println(prefix + "  List:");
        listNode.print(prefix + "    ");
        System.out.println(prefix + "  Value to Add:");
        valuesNode.print(prefix + "    ");
    }

    public void setElementType(Type newType) {
        this.elementType = newType;
    }

    public List<ASTNode> getChildren() {
        return List.of(listNode, valuesNode);
    }

    @Override
    public void bindChildren(StaticContext stx) {
        if (listNode != null) listNode.bind(stx);
        if (valuesNode != null) valuesNode.bind(stx);
    }

    public ASTNode getListNode() {
        return listNode;
    }

    public ASTNode getValuesNode() {
        return valuesNode;
    }

    public Type getElementType() {
        return elementType;
    }
}