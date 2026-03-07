package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;
public class ListSizeNode extends ASTNode {

    private final ASTNode nome; // pode ser variável ou expressão que retorna lista
    private Type type;          // agora Type

    public ListSizeNode(ASTNode nome) {
        this.nome = nome;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ListValue list = (ListValue) nome.evaluate(ctx).value();
        return new TypedValue(PrimitiveTypes.INT, list.size());
    }

    public ASTNode getNome() {
        return nome;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListSize:");
        nome.print(prefix + "  ");
    }

    @Override
    public void bindChildren(StaticContext stx) {
        nome.setParent(this);
        nome.bind(stx);

        Type listType = nome.getType();

        if (listType == null) {
            throw new RuntimeException("ListSize: tipo da lista é null");
        }

        if (!(listType instanceof ListType)) {
            throw new RuntimeException(
                    "ListSize aplicado em tipo não-lista: " + listType.name()
            );
        }

        this.type = PrimitiveTypes.INT;
    }

    @Override
    public Type getType() {
        return type;
    }
}