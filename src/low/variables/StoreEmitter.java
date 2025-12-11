package low.variables;

import java.util.Map;

public class StoreEmitter {
    private final StringEmitter stringEmitter;
    private final Map<String, String> localVars;

    public StoreEmitter(StringEmitter stringEmitter, Map<String, String> localVars) {
        this.stringEmitter = stringEmitter;
        this.localVars = localVars;
    }

    private String getPtr(String name) {
        String ptr = localVars.get(name);
        if(ptr == null) throw new RuntimeException("Pointer not found for variable " + name);
        return ptr;
    }

    public String emit(String name, String type, String value) {
        return switch (type) {
            case "%String*", "%String" -> stringEmitter.emitStore(name, value);
            case "%struct.ArrayListInt*" ->
                    "  store %struct.ArrayListInt* " + value + ", %struct.ArrayListInt** " + getPtr(name) + "\n";
            case "%struct.ArrayListDouble*" ->
                    "  store %struct.ArrayListDouble* " + value + ", %struct.ArrayListDouble** " + getPtr(name) + "\n";
            case "%struct.ArrayListBool*" ->
                    "  store %struct.ArrayListBool* " + value + ", %struct.ArrayListBool** " + getPtr(name) + "\n";
            case "%ArrayList*" ->
                    "  store %ArrayList* " + value + ", %ArrayList** " + getPtr(name) + "\n";
            default ->
                    "  store " + type + " " + value + ", " + type + "* " + getPtr(name) + "\n";
        };
    }

}
