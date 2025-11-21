package low.structs;

import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import low.structs.helpers.StructDefinitionEmitter;
import low.structs.helpers.StructFieldPrint;
import low.structs.helpers.StructTypeResolver;

import java.util.ArrayList;
import java.util.List;
public class StructEmitter {

    private final StructTypeResolver resolver;
    private final StructFieldPrint fieldEmitter;
    private final StructDefinitionEmitter defEmitter;
    private final LLVisitorMain visitorMain;

    public StructEmitter(LLVisitorMain visitorMain) {
        this.visitorMain = visitorMain;
        this.resolver = new StructTypeResolver(visitorMain);
        this.fieldEmitter = new StructFieldPrint(resolver);
        this.defEmitter = new StructDefinitionEmitter(resolver);
    }

    public String emit(StructNode node) {

        if (visitorMain.hasSpecializationFor(node.getName())) {
            System.out.println("[StructEmitter] Ignorando struct genérica: " + node.getName());
            return "";
        }

        String llvmName = node.getLLVMName() != null && !node.getLLVMName().isBlank()
                ? node.getLLVMName()
                : node.getName();

        StringBuilder sb = new StringBuilder();

        sb.append(defEmitter.emitDefinition(node));

        sb.append("define void @print_").append(llvmName)
                .append("(%").append(llvmName).append("* %p) {\nentry:\n");

        for (int i = 0; i < node.getFields().size(); i++) {
            fieldEmitter.emitPrint(sb, i, node.getFields().get(i).getType(), llvmName);
        }

        sb.append("  ret void\n}\n\n");

        return sb.toString();
    }
}
