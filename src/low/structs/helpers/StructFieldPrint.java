package low.structs.helpers;

public class StructFieldPrint {

    private final StructTypeResolver resolver;

    public StructFieldPrint(StructTypeResolver resolver) {
        this.resolver = resolver;
    }

    public void emitPrint(StringBuilder sb, int idx, String fieldType, String llvmName) {

        // GEP do campo
        sb.append("  %f").append(idx)
                .append(" = getelementptr inbounds %").append(llvmName)
                .append(", %").append(llvmName).append("* %p, i32 0, i32 ").append(idx).append("\n");

        // PRIMITIVOS
        switch (fieldType) {
            case "int" -> {
                sb.append("  %v").append(idx).append(" = load i32, i32* %f").append(idx).append("\n");
                sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4x i8], [4x i8]* @.strInt, i32 0, i32 0), i32 %v").append(idx).append(")\n");
                return;
            }
            case "double" -> {
                sb.append("  %v").append(idx).append(" = load double, double* %f").append(idx).append("\n");
                sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4x i8], [4x i8]* @.strDouble, i32 0, i32 0), double %v").append(idx).append(")\n");
                return;
            }
            case "boolean", "bool" -> {
                sb.append("  %v").append(idx).append(" = load i1, i1* %f").append(idx).append("\n");
                sb.append("  %vb").append(idx).append(" = zext i1 %v").append(idx).append(" to i32\n");
                sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4x i8], [4x i8]* @.strInt, i32 0, i32 0), i32 %vb").append(idx).append(")\n");
                return;
            }

            case "string" -> {
                    sb.append("  %v").append(idx).append(" = load %String*, %String** %f").append(idx).append("\n");
                    sb.append("  call void @printString(%String* %v").append(idx).append(")\n");
                    return;
                }
        }

        // STRUCT SIMPLES
        if (fieldType.startsWith("Struct ")) {
            String inner = fieldType.substring(7).trim();
            String innerLLVM = resolver.resolveLLVMName(inner);

            sb.append("  %v").append(idx).append(" = load %").append(innerLLVM)
                    .append("*, %").append(innerLLVM).append("** %f").append(idx).append("\n");
            sb.append("  call void @print_").append(innerLLVM)
                    .append("(%").append(innerLLVM).append("* %v").append(idx).append(")\n");
            return;
        }

        // STRUCT<T>
        if (fieldType.startsWith("Struct<")) {
            String inner = fieldType.substring(7, fieldType.length() - 1).trim();
            String innerLLVM = resolver.resolveLLVMName(inner);

            sb.append("  %v").append(idx).append(" = load %").append(innerLLVM)
                    .append("*, %").append(innerLLVM).append("** %f").append(idx).append("\n");
            sb.append("  call void @print_").append(innerLLVM)
                    .append("(%").append(innerLLVM).append("* %v").append(idx).append(")\n");
            return;
        }

        // LIST<T>
        if (fieldType.startsWith("List<")) {
            String elementType = fieldType.substring(5, fieldType.length() - 1).trim();
            String llvmListType = resolver.toLLVMFieldType(fieldType);

            sb.append("  %v").append(idx).append(" = load ").append(llvmListType)
                    .append(", ").append(llvmListType).append("* %f").append(idx).append("\n");

            switch (elementType) {
                case "int" ->
                        sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* %v").append(idx).append(")\n");
                case "double" ->
                        sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* %v").append(idx).append(")\n");
                case "boolean", "bool" ->
                        sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* %v").append(idx).append(")\n");
                case "string", "String" ->
                        sb.append("  call void @arraylist_print_string(%ArrayList* %v").append(idx).append(")\n");
                case "?" ->
                        sb.append("  ; List<?> genérica: ignorando print\n");
                default ->
                        sb.append("  ; TODO print List<").append(elementType).append(">\n");
            }
            return;
        }

        sb.append("  ; Tipo desconhecido: ").append(fieldType).append("\n");
    }
}
