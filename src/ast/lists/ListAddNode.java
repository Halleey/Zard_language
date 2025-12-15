package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.variables.ListValue;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class ListAddNode extends ASTNode {

    private final ASTNode listNode;
    private final ASTNode valuesNode;
    private String elementType; // novo campo



    public ListAddNode(ASTNode listNode, ASTNode valuesNode, String elementType) {
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

        TypedValue listVal = listNode.evaluate(ctx);

        if (!(listVal.value() instanceof ListValue list)) {
            throw new RuntimeException(
                    "Tentativa de add em algo que não é ListValue: " + listVal.type()
            );
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

    public void setType(String newType) {
        this.elementType = newType;
    }

    public List<ASTNode> getChildren() {
        return List.of(listNode, valuesNode);
    }

    public ASTNode getListNode() {
        return listNode;
    }

    public ASTNode getValuesNode() {
        return valuesNode;
    }

    public String getElementType() {
        return elementType;
    }
}
