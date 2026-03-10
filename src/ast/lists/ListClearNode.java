package ast.lists;

import ast.ASTNode;
import ast.variables.TypeResolver;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
import context.statics.symbols.ListType;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;

public class ListClearNode extends ASTNode {

    private final ASTNode listNode;
    private Type type; // agora Type

    public ListClearNode(ASTNode listNode) {
        this.listNode = listNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ListValue list = (ListValue) listNode.evaluate(ctx).value();
        list.clear();
        return new TypedValue(new ListType(list.getElementType(), list.isReference()), list);
    }

    public ASTNode getListNode() {
        return listNode;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListClear:");
        System.out.println(prefix + "  List:");
        listNode.print(prefix + "    ");
    }

    @Override
    public void bindChildren(StaticContext stx) {
        listNode.setParent(this);
        listNode.bind(stx);

        Type listType = listNode.getType();
        if (listType == null) {
            throw new RuntimeException("ListClear: tipo da lista é null");
        }
        if (!(listType instanceof ListType)) {
            throw new RuntimeException(
                    "ListClear applied in non-list type: " + listType.name()
            );
        }

        this.type = listType; // mantém o mesmo tipo de lista
    }

    @Override
    public Type getType() {
        return type;
    }
}

