package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
import context.statics.symbols.ListType;
import context.statics.symbols.Type;
import context.statics.symbols.UnknownType;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;// Nó AST que representa uma lista tipada


public final class ListNode extends ASTNode {

    private final DynamicList list;
    private Type type;
    private final boolean isReference;

    public ListNode(DynamicList list) {
        this.list = list;
        this.isReference = list.isReference();
    }

    public DynamicList getList() {
        return list;
    }

    public boolean isReference() {
        return isReference;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void bindChildren(StaticContext stx) {
        // Vincula todos os elementos da lista
        for (ASTNode node : list.getElements()) {
            node.bind(stx);
        }

        // Inferir tipo da lista se ainda não definido
        if (list.getElementType() instanceof UnknownType) {
            if (list.getElements().isEmpty()) {
                throw new RuntimeException("Lista vazia sem tipo explícito");
            }

            ASTNode first = list.getElements().get(0);
            Type inferredType = first.getType();

            if (inferredType == null) {
                throw new RuntimeException("Não foi possível inferir o tipo da lista");
            }

            list.lockElementType(inferredType);
        }

        // Agora o tipo do nó é ListType
        this.type = new ListType(list.getElementType(), isReference);
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ListValue value = new ListValue(list.getElementType(), isReference);

        for (ASTNode node : list.getElements()) {
            value.add(node.evaluate(ctx));
        }

        return new TypedValue(type, value); // type agora é Type
    }

    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + type + ":");
        System.out.println(prefix + "ListNode (isReference=" + isReference + ")");
        List<ASTNode> elements = list.getElements();
        if (elements.isEmpty()) {
            System.out.println(prefix + "  (vazia)");
        } else {
            for (int i = 0; i < elements.size(); i++) {
                System.out.print(prefix + "  [" + i + "]: ");
                elements.get(i).print(prefix + "    ");
            }
        }
    }

    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>(list.getElements());
    }
}