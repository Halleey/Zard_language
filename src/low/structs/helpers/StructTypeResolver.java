package low.structs.helpers;

import ast.structs.StructNode;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;

public class StructTypeResolver {

    private final LLVisitorMain visitorMain;
    private final TypeMapper typeMapper;

    public StructTypeResolver(LLVisitorMain visitorMain) {
        this.visitorMain = visitorMain;
        this.typeMapper = new TypeMapper();
    }

    public String resolveLLVMName(String logicalName) {
            StructNode n = visitorMain.getStructNode(logicalName);
        if (n != null && n.getLLVMName() != null && !n.getLLVMName().isBlank()) {
            return n.getLLVMName();
        }
        return logicalName;
    }

    public String toLLVMFieldType(String type) {

        if (type.startsWith("List<")) {
            visitorMain.tiposDeListasUsados.add(type);

            String inner = type.substring(5, type.length() - 1).trim();

            return switch (inner) {
                case "int" -> "%struct.ArrayListInt*";
                case "double" -> "%struct.ArrayListDouble*";
                case "boolean", "bool" -> "%struct.ArrayListBool*";
                case "string", "String", "?" -> "%ArrayList*";
                default -> "%ArrayList*";
            };
        }

        if (type.startsWith("Struct ")) {
            String inner = type.substring(7).trim();
            return "%" + resolveLLVMName(inner) + "*";
        }

        if (type.startsWith("Struct<")) {
            String inner = type.substring(7, type.length() - 1).trim();
            return "%" + resolveLLVMName(inner) + "*";
        }

        return typeMapper.toLLVM(type);
    }
}
