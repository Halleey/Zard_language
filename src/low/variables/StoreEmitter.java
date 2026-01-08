package low.variables;


public class StoreEmitter {
    private final StringEmitter stringEmitter;
    private final VariableEmitter varEmitter; // link para pegar ptrs

    public StoreEmitter(StringEmitter stringEmitter, VariableEmitter varEmitter) {
        this.stringEmitter = stringEmitter;
        this.varEmitter = varEmitter;
    }


    private String getPtr(String name) {
        return varEmitter.getVarPtr(name); // busca na pilha de escopos
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
