package ast.variables;
import ast.ASTNode;
import ast.lists.DynamicList;
import ast.lists.ListNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;

public class VariableDeclarationNode extends ASTNode {
    private final String name;
    private final String type;          // Ex.: int, double, string, List<int>, List<string>
    public final ASTNode initializer;  // pode ser null

    public VariableDeclarationNode(String name, String type, ASTNode initializer) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue value;

        // --- Inicialização ---
        if (initializer != null) {
            value = initializer.evaluate(ctx);

            // Se for lista literal, garante que o tipo bate
            if (initializer instanceof ListNode listNode) {
                String declaredElementType = getListElementType(type); // ex: List<string> → string
                if (!declaredElementType.equals(listNode.getList().getElementType())) {
                    throw new RuntimeException("Tipo incompatível: variável " + name +
                            " declarada como " + type + " mas lista contém " +
                            listNode.getList().getElementType());
                }
                value = new TypedValue(type, listNode.getList());
            }
        } else {
            // Se não há inicializador, cria valor padrão
            value = createDefaultValue(type);
        }

        ctx.declareVariable(name, value);
        return value;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "VarDecl: " + type + " " + name);
        if (initializer != null) {
            System.out.println(prefix + "  Initializer:");
            initializer.print(prefix + "    ");
        }
    }

    // --- Helpers ---

    private TypedValue createDefaultValue(String type) {
        switch (type) {
            case "int" ->      { return new TypedValue("int", 0); }
            case "double" ->   { return new TypedValue("double", 0.0); }
            case "string" ->   { return new TypedValue("string", ""); }
            case "boolean" ->  { return new TypedValue("boolean", false); }
            default -> {
                if (type.startsWith("List<")) {
                    String elementType = getListElementType(type);
                    return new TypedValue(type, new DynamicList(elementType, new ArrayList<>()));
                }
                throw new RuntimeException("Tipo desconhecido: " + type);
            }
        }
    }

    private String getListElementType(String listType) {
        if (!listType.startsWith("List<") || !listType.endsWith(">")) {
            throw new RuntimeException("Tipo inválido de lista: " + listType);
        }
        return listType.substring(5, listType.length() - 1);
    }

    // --- Getters ---
    public String getName() { return name; }
    public String getType() { return type; }
}
