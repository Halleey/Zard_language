package low.module.imports;

import ast.functions.FunctionNode;
import low.module.structs.StructRegistry;

import java.util.Map;

public record ImportRegistry(Map<String, FunctionNode> importedFunctions, StructRegistry structRegistry) {

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
