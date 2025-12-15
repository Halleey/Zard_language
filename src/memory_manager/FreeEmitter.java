package memory_manager;

import ast.structs.StructNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.variables.VariableEmitter;

import java.util.Map;



public class FreeEmitter {

    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final VariableEmitter varEmitter;
    private final LLVisitorMain visitor;

    public FreeEmitter(
            Map<String, TypeInfos> varTypes,
            TempManager temps,
            VariableEmitter varEmitter,
            LLVisitorMain visitor
    ) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.varEmitter = varEmitter;
        this.visitor = visitor;
    }
    public String emit(FreeNode node) {
        String varName = node.getVarName();

        if (visitor.escapesVar(varName)) return "  ; free ignorado (escapa): " + varName + "\n";

        TypeInfos info = varTypes.get(varName);
        if (info == null || info.getSourceType() == null) return "  ; free ignorado (tipo desconhecido): " + varName + "\n";

        String srcType = info.getSourceType();
        if (!srcType.startsWith("Struct<") || !srcType.endsWith(">")) return "  ; free ignorado (não struct): " + varName + "\n";

        String structName = srcType.substring("Struct<".length(), srcType.length() - 1).trim();
        String llvmStructName = structName;

        StructNode def = visitor.getStructNode(structName);
        if (def != null && def.getLLVMName() != null && !def.getLLVMName().isBlank()) {
            llvmStructName = def.getLLVMName().trim();
        }

        String varPtr = varEmitter.getVarPtr(varName);
        if (varPtr == null || varPtr.isBlank()) return "  ; free ignorado (ptr SSA inexistente): " + varName + "\n";

        varPtr = varPtr.trim();
        int cut = varPtr.indexOf(';');
        if (cut != -1) varPtr = varPtr.substring(0, cut).trim();
        if (!varPtr.startsWith("%")) varPtr = "%" + varPtr;

        String loaded = temps.newTemp();

        return """
      ; free %s
      %s = load %%%s*, %%%s** %s
      %s
    """.formatted(
                varName,
                loaded, llvmStructName, llvmStructName, varPtr,
                visitor.emitFreeStruct(loaded, structName)
        );
    }

}
