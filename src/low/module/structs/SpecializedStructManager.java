package low.module.structs;

import ast.structs.StructNode;
import context.statics.symbols.Type;
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

    public StructNode getOrCreateSpecializedStruct(StructNode base, Type elemType) {

        if (base == null || elemType == null) return null;

        // Já é especializado
        if (base.getName().contains("_")) {
            return base;
        }

        String elemName = elemType.toString();

        String key = base.getName() + "<" + elemName + ">";

        if (specializedStructs.containsKey(key)) {
            return specializedStructs.get(key);
        }

        // 🔥 agora passa Type corretamente
        StructNode clone = base.cloneWithType(elemType);

        String llvmName = LLVMNameUtils.llvmSafe(
                base.getName() + "_" + elemName
        );

        clone.setLLVMName(llvmName);

        specializedStructs.put(key, clone);

        String baseName = base.getName();

        structRegistry.put(key, clone);
        structRegistry.put(baseName + "_" + elemName, clone);
        structRegistry.put(llvmName, clone);
        structRegistry.put("%" + llvmName, clone);
        structRegistry.put(baseName + "<" + elemName + ">", clone);

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