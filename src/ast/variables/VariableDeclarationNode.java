package ast.variables;
import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class VariableDeclarationNode extends ASTNode {
    public final String name;
    public final String type;
    public final ASTNode initializer; // pode ser null

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

        if (type.equals("var")) {
            if (initializer == null) {
                throw new RuntimeException("Variável 'var " + name + "' precisa de inicialização para inferir tipo.");
            }
            value = initializer.evaluate(ctx); // pode ser função, número, lista, string, etc.
            ctx.declareVariable(name, value);
            return value;
        }

        // Declaração com tipo explícito
        value = initializer != null ? initializer.evaluate(ctx) : getDefaultValue();

        // Coerção automática int -> double
        if (type.equals("double") && value.getType().equals("int")) {
            value = new TypedValue("double", ((Integer)value.getValue()).doubleValue());
        }

        // Checagem de tipo
        if (!value.getType().equals(type)) {
            throw new RuntimeException("Typing error: " + name +
                    " declarado como " + type +
                    " mas inicializado como " + value.getType());
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

    private TypedValue getDefaultValue() {
        return switch (type) {
            case "int" -> new TypedValue("int", 0);
            case "double" -> new TypedValue("double", 0.0);
            case "string" -> new TypedValue("string", "");
            case "boolean" -> new TypedValue("boolean", false);
            default -> throw new RuntimeException("Tipo desconhecido: " + type);
        };
    }

    public String getName() { return name; }
    public String getType() { return type; }
}
