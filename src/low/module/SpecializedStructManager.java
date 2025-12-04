package low.module;
// package low.module;

import ast.structs.StructNode;
import low.structs.StructEmitter;
import low.utils.LLVMNameUtils;

import java.util.List;
import java.util.Map;

public class SpecializedStructManager {

    private final Map<String, StructNode> specializedStructs;
    private final StructEmitter structEmitter;
    private final StructRegistry structRegistry;
    private final List<String> structDefinitions;

    public SpecializedStructManager(Map<String, StructNode> specializedStructs,
                                    StructEmitter structEmitter,
                                    StructRegistry structRegistry,
                                    List<String> structDefinitions) {
        this.specializedStructs = specializedStructs;
        this.structEmitter = structEmitter;
        this.structRegistry = structRegistry;
        this.structDefinitions = structDefinitions;
    }

    public StructNode getOrCreateSpecializedStruct(StructNode base, String elemType) {
        if (base == null || elemType == null) return null;

        // Proteção: já é especializado (Set_int, etc)
        if (base.getName().contains("_")) {
            return base;
        }

        String key = base.getName() + "<" + elemType + ">";

        if (specializedStructs.containsKey(key)) {
            return specializedStructs.get(key);
        }

        // Clone especializado real
        StructNode clone = base.cloneWithType(elemType);

        // Nome LLVM correto
        String llvmName = LLVMNameUtils.llvmSafe(base.getName() + "_" + elemType);
        clone.setLLVMName(llvmName);

        // Salva no mapa principal de especializações
        specializedStructs.put(key, clone);

        // Registros de lookup compatíveis com o código antigo
        String baseName = base.getName();
        structRegistry.put(key, clone);
        structRegistry.put(baseName + "_" + elemType, clone);
        structRegistry.put(llvmName, clone);
        structRegistry.put("%" + llvmName, clone);
        structRegistry.put(baseName + "<" + elemType + ">", clone);

        // Emite definição apenas uma vez
        String llvmDef = structEmitter.emit(clone);
        structDefinitions.add(llvmDef);

        return clone;
    }

    public boolean hasSpecializationFor(String baseName) {
        for (String key : specializedStructs.keySet()) {
            if (key.startsWith(baseName + "<")) return true;
            if (key.startsWith(baseName + "_")) return true;
        }
        return false;
    }
}
