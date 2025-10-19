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

        // definição do tipo
        sb.append("%").append(node.getName()).append(" = type { ");
        List<String> fieldLLVMTypes = new ArrayList<>();
        for (VariableDeclarationNode field : node.getFields()) {
            fieldLLVMTypes.add(toLLVMFieldType(field.getType()));
        }
        sb.append(String.join(", ", fieldLLVMTypes));
        sb.append(" }\n");

        // gerar função de impressão
        sb.append("define void @print_").append(node.getName())
                .append("(i8* %raw) {\nentry:\n");
        sb.append("  %p = bitcast i8* %raw to %").append(node.getName()).append("*\n");

        for (int i = 0; i < node.getFields().size(); i++) {
            VariableDeclarationNode field = node.getFields().get(i);
            String type = field.getType();
            String fieldName = field.getName();

            sb.append("  %f").append(i).append(" = getelementptr inbounds %")
                    .append(node.getName()).append(", %").append(node.getName())
                    .append("* %p, i32 0, i32 ").append(i).append("\n");

            if (type.equals("int")) {
                sb.append("  %val").append(i).append(" = load i32, i32* %f").append(i).append("\n");
                sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %val").append(i).append(")\n");
            } else if (type.equals("string")) {
                sb.append("  %val").append(i).append(" = load %String*, %String** %f").append(i).append("\n");
                sb.append("  call void @printString(%String* %val").append(i).append(")\n");
            }
            // se houver structs aninhados, chamar recursivamente print_<Struct>
        }

        sb.append("  ret void\n}\n");

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
