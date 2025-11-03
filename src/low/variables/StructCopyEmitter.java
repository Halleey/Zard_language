package low.variables;

import ast.runtime.StructDefinition;
import ast.structs.StructNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.functions.TypeMapper;
import low.main.GlobalStringManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.Map;

public class StructCopyEmitter {
    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final GlobalStringManager globalStrings;
    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper = new TypeMapper();
    public StructCopyEmitter(Map<String, TypeInfos> varTypes, TempManager temps,
                             GlobalStringManager globalStrings, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }

    public String emit(AssignmentNode node, String srcTemp, String dstPtr, String structTypeName) {
        StringBuilder llvm = new StringBuilder();

        //se chegar "Struct<?>"
        String structName = structTypeName.substring(structTypeName.indexOf("<") + 1, structTypeName.indexOf(">")).trim();

        // Recupera definição da struct
        StructNode def = visitor.getStructNode(structName);
        if (def == null) {
            throw new RuntimeException("Struct definition not found for: " + structName);
        }

        llvm.append("  ; Deep copy of struct ").append(structName).append("\n");

        // Carrega destino real (%Struct*)
        String dst = temps.newTemp();
        llvm.append("  ").append(dst)
                .append(" = load %").append(structName)
                .append("*, %").append(structName)
                .append("** ").append(dstPtr).append("\n");

        // Copia campo a campo
        int index = 0;
        for (VariableDeclarationNode field : def.getFields()) {
            String fieldType = typeMapper.toLLVM(field.getType());
            String srcFieldPtr = temps.newTemp();
            String dstFieldPtr = temps.newTemp();
            String val = temps.newTemp();

            llvm.append("  ").append(srcFieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(srcTemp)
                    .append(", i32 0, i32 ").append(index).append("\n");

            llvm.append("  ").append(dstFieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(dst)
                    .append(", i32 0, i32 ").append(index).append("\n");

            llvm.append("  ").append(val)
                    .append(" = load ").append(fieldType)
                    .append(", ").append(fieldType)
                    .append("* ").append(srcFieldPtr).append("\n");

            llvm.append("  store ").append(fieldType).append(" ").append(val)
                    .append(", ").append(fieldType)
                    .append("* ").append(dstFieldPtr).append("\n");

            index++;
        }

        llvm.append(";;VAL:").append(dst)
                .append(";;TYPE:%").append(structName).append("*\n");

        return llvm.toString();
    }
}