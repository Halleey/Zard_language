package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;// Nó AST que representa uma lista tipada

public final class ListNode extends ASTNode {

    private final DynamicList list;
    private String type; // definido após bind()

    public ListNode(DynamicList list) {
        this.list = list;
    }

    public DynamicList getList() {
        return list;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void bindChildren(StaticContext stx) {

        for (ASTNode node : list.getElements()) {
            node.bind(stx);
        }

        if (list.getElementType().equals("?")) {

            if (list.getElements().isEmpty()) {
                throw new RuntimeException(
                        "Lista vazia sem tipo explícito"
                );
            }

            ASTNode first = list.getElements().get(0);
            String inferredType = first.getType();

            if (inferredType == null) {
                throw new RuntimeException(
                        "Não foi possível inferir o tipo da lista"
                );
            }

            list.lockElementType(inferredType);
        }

        this.type = "List<" + list.getElementType() + ">";
    }

    private String getString() {
        String expected = list.getElementType();

        for (ASTNode node : list.getElements()) {
            String nodeType = node.getType();

            if (nodeType == null) {
                throw new RuntimeException(
                        "Elemento da lista sem tipo inferido"
                );
            }

            if (!nodeType.equals(expected)) {
                throw new RuntimeException(
                        "Tipo inválido na lista <" + expected + ">: " + nodeType
                );
            }
        }
        return expected;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        ListValue value = new ListValue(list.getElementType());

        for (ASTNode node : list.getElements()) {
            value.add(node.evaluate(ctx));
        }

        return new TypedValue(type, value);
    }


    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + type + ":");

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
        // retorna os elementos da lista como filhos
        return new ArrayList<>(list.getElements());
    }


}
