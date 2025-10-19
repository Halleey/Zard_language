package low.structs;

import ast.structs.StructFieldAccessNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;

import java.util.List;


public class StructFieldAccessEmitter {
    private final TempManager temps;

    public StructFieldAccessEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(StructFieldAccessNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        // Avalia a instância da struct
        String structCode = node.getStructInstance().accept(visitor);
        llvm.append(structCode);

        String structTemp = extractTemp(structCode);
        String structType = extractType(structCode);

        String cleanName = structType.replace("*", "").replace("%", "");
        if (cleanName.contains("_") && !cleanName.startsWith("Struct<")) {
            cleanName = cleanName.replace("_", ".");
        }


        if (cleanName.startsWith("Struct<") && cleanName.endsWith(">")) {
            cleanName = cleanName.substring(7, cleanName.length() - 1);
        }

        StructNode def = visitor.getStructNode(cleanName);
        if (def == null) {
            throw new RuntimeException("Struct definition not found for type: " + cleanName);
        }

        List<VariableDeclarationNode> fields = def.getFields();

        int fieldIndex = -1;
        VariableDeclarationNode fieldDecl = null;
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(node.getFieldName())) {
                fieldIndex = i;
                fieldDecl = fields.get(i);
                break;
            }
        }
        if (fieldIndex == -1) {
            throw new RuntimeException("Campo não encontrado: " + node.getFieldName());
        }

        String fieldPtr = temps.newTemp();
        llvm.append("  ")
                .append(fieldPtr)
                .append(" = getelementptr inbounds ")
                .append(structType.replace("*", "")) // tipo base
                .append(", ")
                .append(structType)
                .append(" ")
                .append(structTemp)
                .append(", i32 0, i32 ")
                .append(fieldIndex)
                .append("\n");

        String fieldLLVMType = new TypeMapper().toLLVM(fieldDecl.getType());

        if (node.getValue() != null) {
            String valCode = node.getValue().accept(visitor);
            llvm.append(valCode);
            String valTemp = extractTemp(valCode);
            llvm.append("  store ")
                    .append(fieldLLVMType).append(" ").append(valTemp)
                    .append(", ").append(fieldLLVMType).append("* ").append(fieldPtr)
                    .append("\n");
            llvm.append(";;VAL:").append(valTemp).append(";;TYPE:").append(fieldLLVMType).append("\n");
        } else {

            String tmp = temps.newTemp();
            llvm.append("  ")
                    .append(tmp)
                    .append(" = load ")
                    .append(fieldLLVMType)
                    .append(", ")
                    .append(fieldLLVMType)
                    .append("* ")
                    .append(fieldPtr)
                    .append("\n");
            llvm.append(";;VAL:").append(tmp).append(";;TYPE:").append(fieldLLVMType).append("\n");
        }

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        return code.substring(lastTypeIdx + 7).trim();
    }
}
