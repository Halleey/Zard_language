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

        String llvmName = (node.getLLVMName() != null && !node.getLLVMName().isBlank())
                ? node.getLLVMName()
                : node.getName();

        System.out.println("[StructEmitter] Emitindo struct: " + node.getName() + " -> %" + llvmName);

        // ===== Definição do tipo =====
        sb.append("%").append(llvmName).append(" = type { ");
        List<String> fieldLLVMTypes = new ArrayList<>();
        for (VariableDeclarationNode field : node.getFields()) {
            String llvmType = toLLVMFieldType(field.getType());
            fieldLLVMTypes.add(llvmType);
            System.out.println("  [Field] " + field.getName() + " : " + field.getType() + " -> " + llvmType);
        }
        sb.append(String.join(", ", fieldLLVMTypes));
        sb.append(" }\n\n");

        // ===== Função de impressão =====
        sb.append("define void @print_").append(llvmName)
                .append("(%").append(llvmName).append("* %p) {\nentry:\n");

        for (int i = 0; i < node.getFields().size(); i++) {
            VariableDeclarationNode field = node.getFields().get(i);
            String type = field.getType();

            sb.append("  %f").append(i).append(" = getelementptr inbounds %")
                    .append(llvmName).append(", %").append(llvmName)
                    .append("* %p, i32 0, i32 ").append(i).append("\n");

            // ---------- campos primitivos ----------
            if (type.equals("int")) {
                sb.append("  %val").append(i).append(" = load i32, i32* %f").append(i).append("\n");
                sb.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), ")
                        .append("i32 %val").append(i).append(")\n");
            } else if (type.equals("double")) {
                sb.append("  %val").append(i).append(" = load double, double* %f").append(i).append("\n");
                sb.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), ")
                        .append("double %val").append(i).append(")\n");
            } else if (type.equals("boolean") || type.equals("bool")) {
                sb.append("  %val").append(i).append(" = load i1, i1* %f").append(i).append("\n");
                // Simples: imprime como 0/1 por enquanto, ou usa printString se tiver
                sb.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), ")
                        .append("i32 zext (i1 %val").append(i).append(" to i32))\n");
            } else if (type.equals("string")) {
                sb.append("  %val").append(i).append(" = load %String*, %String** %f").append(i).append("\n");
                sb.append("  call void @printString(%String* %val").append(i).append(")\n");
            }

            // ---------- Struct X ----------
            else if (type.startsWith("Struct ")) {
                String inner = type.substring("Struct ".length()).trim();
                String innerLLVM = resolveStructLLVMName(inner);
                sb.append("  %val").append(i).append(" = load %").append(innerLLVM)
                        .append("*, %").append(innerLLVM).append("** %f").append(i).append("\n");
                sb.append("  call void @print_").append(innerLLVM)
                        .append("(%").append(innerLLVM).append("* %val").append(i).append(")\n");
            }

            // ---------- Struct<Y> ----------
            else if (type.startsWith("Struct<") && type.endsWith(">")) {
                String inner = type.substring(7, type.length() - 1).trim();
                String innerLLVM = resolveStructLLVMName(inner);
                sb.append("  %val").append(i).append(" = load %").append(innerLLVM)
                        .append("*, %").append(innerLLVM).append("** %f").append(i).append("\n");
                sb.append("  call void @print_").append(innerLLVM)
                        .append("(%").append(innerLLVM).append("* %val").append(i).append(")\n");
            }

            // ---------- List<T> ----------
            else if (type.startsWith("List<")) {
                String elementType = type.substring(5, type.length() - 1).trim();
                String listLLVMType = toLLVMFieldType(type); // mesmo mapeamento do campo

                // load com o tipo correto (coerente com a definição do struct)
                sb.append("  %val").append(i).append(" = load ")
                        .append(listLLVMType).append(", ")
                        .append(listLLVMType).append("* %f").append(i).append("\n");

                // Escolhe a função de impressão adequada
                if (elementType.equals("int")) {
                    sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* %val")
                            .append(i).append(")\n");
                } else if (elementType.equals("double")) {
                    sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* %val")
                            .append(i).append(")\n");
                } else if (elementType.equals("boolean") || elementType.equals("bool")) {
                    sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* %val")
                            .append(i).append(")\n");
                } else if (elementType.equals("string") || elementType.equals("String")) {
                    sb.append("  call void @arraylist_print_string(%ArrayList* %val")
                            .append(i).append(")\n");
                } else if (elementType.startsWith("Struct")) {
                    // Se quiser tratar List<Struct<...>> no futuro, dá pra usar arraylist_print_ptr aqui.
                    sb.append("  ; TODO: imprimir List<").append(elementType).append("> via arraylist_print_ptr\n");
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
            String inner = type.substring("Struct ".length()).trim();
            String innerLLVM = resolveStructLLVMName(inner);
            return "%" + innerLLVM + "*";
        }

        if (type.startsWith("Struct<") && type.endsWith(">")) {
            String inner = type.substring(7, type.length() - 1).trim();
            String innerLLVM = resolveStructLLVMName(inner);
            return "%" + innerLLVM + "*";
        }

        return typeMapper.toLLVM(type);
    }

    private String resolveStructLLVMName(String logicalName) {
        StructNode n = visitorMain.getStructNode(logicalName);
        if (n != null && n.getLLVMName() != null && !n.getLLVMName().isBlank()) {
            System.out.println("[StructEmitter] Resolved struct LLVM name: " + logicalName + " -> " + n.getLLVMName());
            return n.getLLVMName();
        }
        return logicalName;
    }
}
