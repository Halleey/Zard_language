package low.module;
import low.main.TypeInfos;
import java.util.HashMap;
import java.util.Map;

public class TypeTable {

    private final Map<String, TypeInfos> varTypes;
    private final Map<String, TypeInfos> functionTypes;

    public TypeTable() {
        this(new HashMap<>(), new HashMap<>());
    }

    public TypeTable(Map<String, TypeInfos> varTypes,
                     Map<String, TypeInfos> functionTypes) {
        this.varTypes = varTypes;
        this.functionTypes = functionTypes;
    }

    public void putVarType(String name, TypeInfos type) {
        varTypes.put(name, type);
    }

    public TypeInfos getVarType(String name) {
        return varTypes.get(name);
    }

    public Map<String, TypeInfos> getVarTypesMap() {
        return varTypes;
    }

    public void putFunctionType(String name, TypeInfos type) {
        functionTypes.put(name, type);
    }

    public TypeInfos getFunctionType(String name) {
        return functionTypes.get(name);
    }

    public Map<String, TypeInfos> getFunctionTypesMap() {
        return functionTypes;
    }
}
