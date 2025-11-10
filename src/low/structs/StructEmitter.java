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
        sb.append(" }\n\n");

        sb.append("define void @print_").append(node.getName())
                .append("(%").append(node.getName()).append("* %p) {\nentry:\n");

        for (int i = 0; i < node.getFields().size(); i++) {
            VariableDeclarationNode field = node.getFields().get(i);
            String type = field.getType();

            sb.append("  %f").append(i).append(" = getelementptr inbounds %")
                    .append(node.getName()).append(", %").append(node.getName())
                    .append("* %p, i32 0, i32 ").append(i).append("\n");

            if (type.equals("int")) {
                sb.append("  %val").append(i).append(" = load i32, i32* %f").append(i).append("\n");
                sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr "
                        + "([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %val").append(i).append(")\n");
            } else if (type.equals("string")) {
                sb.append("  %val").append(i).append(" = load %String*, %String** %f").append(i).append("\n");
                sb.append("  call void @printString(%String* %val").append(i).append(")\n");
            }

            else if (type.startsWith("Struct ")) {
                String inner = type.substring("Struct ".length()).trim();
                sb.append("  %val").append(i).append(" = load %").append(inner)
                        .append("*, %").append(inner).append("** %f").append(i).append("\n");
                sb.append("  call void @print_").append(inner)
                        .append("(%").append(inner).append("* %val").append(i).append(")\n");
            } else if (type.startsWith("Struct<") && type.endsWith(">")) {
                String inner = type.substring(7, type.length() - 1).trim();
                sb.append("  %val").append(i).append(" = load %").append(inner)
                        .append("*, %").append(inner).append("** %f").append(i).append("\n");
                sb.append("  call void @print_").append(inner)
                        .append("(%").append(inner).append("* %val").append(i).append(")\n");
            }

            else if (type.startsWith("List<")) {
                String elementType = type.substring(5, type.length() - 1).trim();
                sb.append("  %val").append(i).append(" = load %ArrayList*, %ArrayList** %f").append(i).append("\n");

                // strings e tipos primitivos específicos
                if (elementType.equals("string") || elementType.equals("String")) {
                    sb.append("  call void @arraylist_print_string(%ArrayList* %val").append(i).append(")\n");
                } else if (elementType.equals("int")) {
                    sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* %val").append(i).append(")\n");
                } else if (elementType.equals("double")) {
                    sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* %val").append(i).append(")\n");
                } else if (elementType.equals("boolean")) {
                    sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* %val").append(i).append(")\n");
                } else if (elementType.startsWith("Struct")) {
                    // listas de structs: imprime via ponteiro + função tipada
                    String inner = elementType
                            .replace("Struct<", "")
                            .replace("Struct ", "")
                            .replace(">", "")
                            .trim();
                    sb.append("  call void @arraylist_print_ptr(%ArrayList* %val")
                            .append(i)
                            .append(", void (i8*)* @print_").append(inner).append(")\n");
                } else {
                    sb.append("  ; Unrecognized list type: ").append(elementType).append("\n");
                }
            }
        }

        sb.append("  ret void\n}\n\n");
        return sb.toString();
    }

    private String toLLVMFieldType(String type) {
        if (type.startsWith("List<")) {
            String innerType = type.substring(5, type.length() - 1).trim();
            visitorMain.tiposDeListasUsados.add(type);
            return "%ArrayList*";
        }
        if (type.startsWith("Struct ")) {
            String inner = type.substring("Struct ".length()).trim();
            return "%" + inner + "*";
        }
        if (type.startsWith("Struct<") && type.endsWith(">")) {
            String inner = type.substring(7, type.length() - 1).trim();
            return "%" + inner + "*";
        }
        return typeMapper.toLLVM(type);
    }
}