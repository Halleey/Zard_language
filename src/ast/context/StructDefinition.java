package ast.context;

import ast.variables.VariableDeclarationNode;

import java.util.List;

public class StructDefinition {
    private final String name;
    private final List<VariableDeclarationNode> fields;

    public StructDefinition(String name, List<VariableDeclarationNode> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public List<VariableDeclarationNode> getFields() {
        return fields;
    }
}
