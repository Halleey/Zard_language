package low.structs.helpers;

import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;

import java.util.ArrayList;
import java.util.List;

public class StructDefinitionEmitter {

    private final StructTypeResolver resolver;

    public StructDefinitionEmitter(StructTypeResolver resolver) {
        this.resolver = resolver;
    }

    public String emitDefinition(StructNode node) {

        String llvmName = node.getLLVMName() != null && !node.getLLVMName().isBlank()
                ? node.getLLVMName()
                : node.getName();

        StringBuilder sb = new StringBuilder();
        sb.append("%").append(llvmName).append(" = type { ");

        List<String> fieldTypes = new ArrayList<>();

        for (VariableDeclarationNode field : node.getFields()) {
            fieldTypes.add(resolver.toLLVMFieldType(field.getType()));
        }

        sb.append(String.join(", ", fieldTypes));
        sb.append(" }\n\n");
        return sb.toString();
    }
}
