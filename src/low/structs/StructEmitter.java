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

        if (visitorMain.hasSpecializationFor(node.getName())) {
            System.out.println("[StructEmitter] Ignorando struct genérica: " + node.getName());
            return "";
        }


        StringBuilder sb = new StringBuilder();

        String llvmName = (node.getLLVMName() != null && !node.getLLVMName().isBlank())
                ? node.getLLVMName()
                : node.getName();

        System.out.println("[StructEmitter] Emitindo struct: " + node.getName() + " -> %" + llvmName);

        sb.append("%").append(llvmName).append(" = type { ");
        List<String> fieldLLVMTypes = new ArrayList<>();

        for (VariableDeclarationNode field : node.getFields()) {
            String llvmType = toLLVMFieldType(field.getType());
            fieldLLVMTypes.add(llvmType);
            System.out.println("  [Field] " + field.getName() + " : " + field.getType() + " -> " + llvmType);
        }

        sb.append(String.join(", ", fieldLLVMTypes));
        sb.append(" }\n\n");

        sb.append("define void @print_").append(llvmName)
                .append("(%").append(llvmName).append("* %p) {\nentry:\n");

        for (int i = 0; i < node.getFields().size(); i++) {
            VariableDeclarationNode field = node.getFields().get(i);
            String type = field.getType();

            sb.append("  %f").append(i).append(" = getelementptr inbounds %")
                    .append(llvmName).append(", %").append(llvmName)
                    .append("* %p, i32 0, i32 ").append(i).append("\n");

            emitPrintField(sb, i, type);
        }

        sb.append("  ret void\n}\n\n");
        return sb.toString();
    }

    private void emitPrintField(StringBuilder sb, int i, String type) {

        // tipo primitivo
        if (type.equals("int")) {
            sb.append("  %val").append(i).append(" = load i32, i32* %f").append(i).append("\n");
            sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4x i8], [4x i8]* @.strInt, i32 0, i32 0), i32 %val")
                    .append(i).append(")\n");
            return;
        }

        if (type.equals("double")) {
            sb.append("  %val").append(i).append(" = load double, double* %f").append(i).append("\n");
            sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4x i8], [4x i8]* @.strDouble, i32 0, i32 0), double %val")
                    .append(i).append(")\n");
            return;
        }

        if (type.equals("bool") || type.equals("boolean")) {
            sb.append("  %val").append(i).append(" = load i1, i1* %f").append(i).append("\n");
            sb.append("  %bext").append(i).append(" = zext i1 %val").append(i).append(" to i32\n");
            sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4x i8], [4x i8]* @.strInt, i32 0, i32 0), i32 %bext")
                    .append(i).append(")\n");
            return;
        }

        if (type.equals("string")) {
            sb.append("  %val").append(i).append(" = load %String*, %String** %f").append(i).append("\n");
            sb.append("  call void @printString(%String* %val").append(i).append(")\n");
            return;
        }

        // Struct simples
        if (type.startsWith("Struct ") ) {
            String inner = type.substring("Struct ".length()).trim();
            String innerLLVM = resolveStructLLVMName(inner);

            sb.append("  %val").append(i).append(" = load %").append(innerLLVM)
                    .append("*, %").append(innerLLVM).append("** %f").append(i).append("\n");
            sb.append("  call void @print_").append(innerLLVM)
                    .append("(%").append(innerLLVM).append("* %val").append(i).append(")\n");
            return;
        }

        // Struct<T>
        if (type.startsWith("Struct<")) {
            String inner = type.substring(7, type.length() - 1).trim();
            String innerLLVM = resolveStructLLVMName(inner);

            sb.append("  %val").append(i).append(" = load %").append(innerLLVM)
                    .append("*, %").append(innerLLVM).append("** %f").append(i).append("\n");
            sb.append("  call void @print_").append(innerLLVM)
                    .append("(%").append(innerLLVM).append("* %val").append(i).append(")\n");
            return;
        }

        // List<T>
        if (type.startsWith("List<")) {
            String elementType = type.substring(5, type.length() - 1).trim();
            String listLLVMType = toLLVMFieldType(type);

            sb.append("  %val").append(i).append(" = load ")
                    .append(listLLVMType).append(", ")
                    .append(listLLVMType).append("* %f").append(i).append("\n");

            switch (elementType) {
                case "int" ->
                        sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* %val").append(i).append(")\n");
                case "double" ->
                        sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* %val").append(i).append(")\n");
                case "boolean", "bool" ->
                        sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* %val").append(i).append(")\n");

                case "string", "String" -> {
                    sb.append("  ; TODO: imprimir List<string> (nenhuma função runtime definida ainda)\n");
                }

                case "?" -> {
                    sb.append("  ; List<?> genérica: sem tipo concreto, não imprimindo este campo\n");
                }

                default ->
                        sb.append("  ; TODO: imprimir List<").append(elementType).append(">\n");
            }
            return;
        }

        sb.append("  ; Tipo não reconhecido no print: ").append(type).append("\n");
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
            return "%" + resolveStructLLVMName(type.substring(7).trim()) + "*";
        }

        if (type.startsWith("Struct<")) {
            return "%" + resolveStructLLVMName(type.substring(7, type.length() - 1).trim()) + "*";
        }

        return typeMapper.toLLVM(type);
    }

    private String resolveStructLLVMName(String logicalName) {
        StructNode n = visitorMain.getStructNode(logicalName);
        if (n != null && n.getLLVMName() != null && !n.getLLVMName().isBlank()) {
            return n.getLLVMName();
        }
        return logicalName;
    }
}
