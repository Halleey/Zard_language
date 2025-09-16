package variables;
import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

import java.util.Map;
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
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue value = initializer != null ? initializer.evaluate(ctx) : getDefaultValue();
        ctx.declareVariable(name, value);
        return value;
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
}


