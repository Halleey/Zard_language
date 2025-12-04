package low.module;
// package low.module;

import ast.functions.FunctionNode;

import java.util.Map;

public class ImportRegistry {

    private final Map<String, FunctionNode> importedFunctions;
    private final StructRegistry structRegistry;

    public ImportRegistry(Map<String, FunctionNode> importedFunctions,
                          StructRegistry structRegistry) {
        this.importedFunctions = importedFunctions;
        this.structRegistry = structRegistry;
    }

    public void registerImportedFunction(String qualifiedName, FunctionNode func) {
        if (qualifiedName == null || func == null) return;
        importedFunctions.put(qualifiedName, func);
    }

    public FunctionNode getImportedFunction(String qualifiedName) {
        if (qualifiedName == null) return null;
        return importedFunctions.get(qualifiedName);
    }

    public void markStructImported(String name) {
        structRegistry.markImported(name);
    }
}
