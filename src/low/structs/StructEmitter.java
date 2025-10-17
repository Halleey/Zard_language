package low.structs;

import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.List;

public class StructEmitter {
    private final LLVisitorMain visitorMain;
    private final TypeMapper typeMapper = new TypeMapper();

    public StructEmitter(LLVisitorMain visitorMain) {
        this.visitorMain = visitorMain;
    }

    public String emit(StructNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("%").append(node.getName()).append(" = type { ");

        List<String> fieldLLVMTypes = new ArrayList<>();
        for (VariableDeclarationNode field : node.getFields()) {
            fieldLLVMTypes.add(toLLVMFieldType(field.getType()));
        }
        sb.append(String.join(", ", fieldLLVMTypes));
        sb.append(" }\n");
        return sb.toString();
    }


    private String toLLVMFieldType(String type) {
        if (type.startsWith("List<")) {
            String innerType = type.substring(5, type.length() - 1).trim();
            switch (innerType) {
                case "int" -> {
                    visitorMain.tiposDeListasUsados.add(type);
                    return "%struct.ArrayListInt*";
                }
                case "double" -> {
                    visitorMain.tiposDeListasUsados.add(type);
                    return "%struct.ArrayListDouble*";
                }
                case "boolean" -> {
                    visitorMain.tiposDeListasUsados.add(type);
                    return "%struct.ArrayListBool*";
                }
                case "string" -> {
                    visitorMain.tiposDeListasUsados.add(type);
                    return "%ArrayList*";
                }
                default -> {
                    visitorMain.tiposDeListasUsados.add(type);
                    return "%ArrayList*";
                }
            }
        }
        return typeMapper.toLLVM(type);
    }
}
