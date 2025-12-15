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

        if (visitor.escapesVar(varName))
            return "  ; free ignorado (escapa): " + varName + "\n";

        TypeInfos info = varTypes.get(varName);
        if (info == null || info.getSourceType() == null)
            return "  ; free ignorado (tipo desconhecido): " + varName + "\n";

        String srcType = info.getSourceType();

        // ===== STRING =====
        if (srcType.equals("string")) {
            return emitFreeString(varName);
        }

        // ===== LIST =====
        if (srcType.startsWith("List<") && srcType.endsWith(">")) {
            return emitFreeList(varName, info);
        }

        // ===== STRUCT =====
        if (srcType.startsWith("Struct<") && srcType.endsWith(">")) {
            return emitFreeStruct(varName, info);
        }

        return "  ; free ignorado (tipo não gerenciado): " + varName + "\n";
    }

    private String emitFreeStruct(String varName, TypeInfos info) {

        String srcType = info.getSourceType();
        String structName =
                srcType.substring("Struct<".length(), srcType.length() - 1).trim();

        StructNode def = visitor.getStructNode(structName);
        String llvmStructName =
                (def != null && def.getLLVMName() != null && !def.getLLVMName().isBlank())
                        ? def.getLLVMName().trim()
                        : structName;

        String varPtr = varEmitter.getVarPtr(varName);
        if (varPtr == null || varPtr.isBlank())
            return "  ; free ignorado (ptr SSA inexistente): " + varName + "\n";

        varPtr = sanitizePtr(varPtr);

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


    private String emitFreeList(String varName, TypeInfos info) {

        if (visitor.escapesVar(varName))
            return "  ; free ignorado (escapa): " + varName + "\n";

        String varPtr = varEmitter.getVarPtr(varName);
        if (varPtr == null || varPtr.isBlank())
            return "  ; free ignorado (ptr SSA inexistente): " + varName + "\n";

        varPtr = sanitizePtr(varPtr);

        String elemType = info.getSourceType()
                .substring("List<".length(), info.getSourceType().length() - 1)
                .trim();

        String llvmListType;
        String freeFn;

        switch (elemType) {
            case "int" -> {
                llvmListType = "%struct.ArrayListInt";
                freeFn = "arraylist_free_int";
            }
            case "double" -> {
                llvmListType = "%struct.ArrayListDouble";
                freeFn = "arraylist_free_double";
            }
            case "bool" -> {
                llvmListType = "%struct.ArrayListBool";
                freeFn = "arraylist_free_bool";
            }
            default -> {
                llvmListType = "%ArrayList";
                freeFn = "freeList";
            }
        }

        String loaded = temps.newTemp();

        return """
                  ; free list %s
                  %s = load %s*, %s** %s
                  call void @%s(%s* %s)
                """.formatted(
                varName,
                loaded, llvmListType, llvmListType, varPtr,
                freeFn, llvmListType, loaded
        );
    }

    private String sanitizePtr(String ptr) {
        ptr = ptr.trim();
        int cut = ptr.indexOf(';');
        if (cut != -1) ptr = ptr.substring(0, cut).trim();
        if (!ptr.startsWith("%")) ptr = "%" + ptr;
        return ptr;
    }

    private String emitFreeString(String varName) {

        if (visitor.escapesVar(varName))
            return "  ; free ignorado (string escapa): " + varName + "\n";

        String varPtr = varEmitter.getVarPtr(varName);
        if (varPtr == null || varPtr.isBlank())
            return "  ; free ignorado (ptr SSA inexistente): " + varName + "\n";

        varPtr = sanitizePtr(varPtr);

        String strObj = temps.newTemp(); // %String*
        String fieldPtr = temps.newTemp(); // i8**
        String raw = temps.newTemp(); // i8*

        return """
                  ; free string %s
                  %s = load %%String*, %%String** %s
                  %s = getelementptr inbounds %%String, %%String* %s, i32 0, i32 0
                  %s = load i8*, i8** %s
                  call void @free(i8* %s)
                """.formatted(
                varName,
                strObj, varPtr,
                fieldPtr, strObj,
                raw, fieldPtr,
                raw
        );
    }
}
