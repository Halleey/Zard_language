package low.module.structs;
import ast.structs.StructNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StructRegistry {

    private final Map<String, StructNode> structNodes;
    private final Set<String> usedStructs;
    private final Set<String> importedStructs;

    public StructRegistry() {
        this(new HashMap<>(), new HashSet<>(), new HashSet<>());
    }

    public StructRegistry(Map<String, StructNode> structNodes,
                          Set<String> usedStructs,
                          Set<String> importedStructs) {
        this.structNodes = structNodes;
        this.usedStructs = usedStructs;
        this.importedStructs = importedStructs;
    }

    public void put(String name, StructNode node) {
        if (name == null || node == null) return;
        structNodes.put(name, node);
    }

    public StructNode get(String name) {
        if (name == null) return null;
        return structNodes.get(name);
    }

    public void markUsed(String name) {
        if (name == null) return;
        usedStructs.add(name);
    }

    public void markImported(String name) {
        if (name == null) return;
        importedStructs.add(name);
        usedStructs.add(name); // import implica uso
    }

    public boolean isUsed(String name) {
        if (name == null) return false;
        return usedStructs.contains(name);
    }

    public Map<String, StructNode> getStructMap() {
        return structNodes;
    }
}
