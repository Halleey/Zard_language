package low.main;

public class TypeInfos {
    private final String sourceType;   // tipo semantico: List<string>, Struct Pessoa, int
    private final String llvmType;     // tipo fisico em LLVM i8*, %String*, i32
    private final String elementType;  // se for List<T> -> string, Pessoa
    private final boolean isList;
    private final boolean isStruct;

    public TypeInfos(String sourceType, String llvmType, String elementType) {
        this.sourceType = sourceType;
        this.llvmType = llvmType;
        this.elementType = elementType;
        this.isList = sourceType.startsWith("List<");
        this.isStruct = sourceType.startsWith("Struct");
    }

    public String getSourceType() { return sourceType; }
    public String getLLVMType() { return llvmType; }
    public String getElementType() { return elementType; }
    public boolean isList() { return isList; }
    public boolean isStruct() { return isStruct; }

    @Override
    public String toString() {
        return "TypeInfo{source=" + sourceType + ", llvm=" + llvmType + "}";
    }
}
