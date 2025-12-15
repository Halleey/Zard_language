package low.variables.structs;


import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.functions.TypeMapper;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.Map;
import java.util.Map;

public class StructInitEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final Map<String, String> localVars;
    private final TypeMapper mapper = new TypeMapper();

    public StructInitEmitter(
            TempManager temps,
            LLVisitorMain visitor,
            Map<String, String> localVars
    ) {
        this.temps = temps;
        this.visitor = visitor;
        this.localVars = localVars;
    }

    private String getVarPtr(String name) {
        String ptr = localVars.get(name);
        if (ptr == null) {
            throw new RuntimeException("Ptr não encontrado para variável: " + name);
        }
        return ptr;
    }

    /**
     * Inicializa variável Struct (stack vs heap) e inicializa campos List<...>.
     * IMPORTANTE:
     * - arraylist_create(...) retorna i8* (genérico) -> precisa bitcast para %ArrayList*
     * - arraylist_create_int/double/bool retornam struct ponteiro diretamente
     */
    public String emit(VariableDeclarationNode node, TypeInfos info) {

        StringBuilder sb = new StringBuilder();

        String varName = node.getName();
        String srcType = info.getSourceType();   // Ex: Struct<Set<Item>>  (ou Struct<Row>)
        String llvmType = info.getLLVMType();    // Ex: %Set_Item* (ou %Row*)
        String varPtr = getVarPtr(varName);

        boolean escapes = visitor.escapesVar(varName);

        // ---- resolve nome base da struct do SourceType
        // Aceita Struct<X> e Struct<X<Y>> etc.
        if (!srcType.startsWith("Struct<") || !srcType.endsWith(">")) {
            throw new RuntimeException("StructInitEmitter recebeu tipo inesperado: " + srcType);
        }

        String structName = srcType.substring("Struct<".length(), srcType.length() - 1).trim();

        // Pega a definição pela BASE (ex: "Set" vindo de "Set<Item>")
        String baseName = structName;
        int genIdx = structName.indexOf('<');
        if (genIdx != -1) baseName = structName.substring(0, genIdx).trim();

        StructNode structDef = visitor.getStructNode(baseName);
        if (structDef == null) {
            throw new RuntimeException("Struct não encontrada: " + baseName + " (de " + structName + ")");
        }

        // llvmType deve ser ponteiro
        if (!llvmType.endsWith("*")) {
            throw new RuntimeException("LLVM type não é ponteiro para struct: " + llvmType);
        }

        String structLLVMPtr = llvmType;                 // %Set_Item*
        String structLLVM = llvmType.substring(0, llvmType.length() - 1); // %Set_Item

        String objPtr = temps.newTemp();

        /* ===============================
           ALOCAÇÃO: STACK vs HEAP
           =============================== */
        if (!escapes) {
            sb.append("  ").append(objPtr)
                    .append(" = alloca ").append(structLLVM).append("\n");
        } else {
            String gepTmp = temps.newTemp();
            String sizeTmp = temps.newTemp();
            String rawPtr = temps.newTemp();

            sb.append("  ").append(gepTmp)
                    .append(" = getelementptr ").append(structLLVM)
                    .append(", ").append(structLLVM).append("* null, i32 1\n");

            sb.append("  ").append(sizeTmp)
                    .append(" = ptrtoint ").append(structLLVM)
                    .append("* ").append(gepTmp).append(" to i64\n");

            sb.append("  ").append(rawPtr)
                    .append(" = call i8* @malloc(i64 ").append(sizeTmp).append(")\n");

            sb.append("  ").append(objPtr)
                    .append(" = bitcast i8* ").append(rawPtr)
                    .append(" to ").append(structLLVM).append("*\n");
        }

        sb.append(";;VAL:").append(objPtr)
                .append(";;TYPE:").append(structLLVM).append("*\n");

        /* ===============================
           DEFAULTS TRIVIAIS (opcional, mas bom)
           =============================== */
        // Se quiser: zera campos primitivos e string.
        // Listas serão inicializadas abaixo.
        var fields = structDef.getFields();
        for (int i = 0; i < fields.size(); i++) {
            VariableDeclarationNode field = fields.get(i);
            String fieldType = field.getType();

            if (fieldType.startsWith("List<")) continue; // lista tem init específico abaixo

            String fieldLLVM = mapFieldTypeForStruct(fieldType);
            String defaultVal = emitDefaultValue(fieldType, sb);

            String fieldPtr = temps.newTemp();
            sb.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVM).append(", ").append(structLLVM)
                    .append("* ").append(objPtr)
                    .append(", i32 0, i32 ").append(i).append("\n");

            sb.append("  store ").append(fieldLLVM).append(" ").append(defaultVal)
                    .append(", ").append(fieldLLVM).append("* ").append(fieldPtr).append("\n");
        }

        /* ===============================
           INICIALIZA CAMPOS (listas)
           =============================== */
        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            String fieldType = field.getType();

            if (!fieldType.startsWith("List<")) continue;

            String elemType = fieldType.substring(5, fieldType.length() - 1).trim();
            visitor.registerListElementType(varName, elemType);

            String fieldPtr = temps.newTemp();
            sb.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVM).append(", ").append(structLLVM)
                    .append("* ").append(objPtr)
                    .append(", i32 0, i32 ").append(i).append("\n");

            // ---- cria lista do tipo correto
            switch (elemType) {
                case "int" -> {
                    String listTmp = temps.newTemp();
                    sb.append("  ").append(listTmp)
                            .append(" = call %struct.ArrayListInt* @arraylist_create_int(i64 10)\n");

                    sb.append("  store %struct.ArrayListInt* ").append(listTmp)
                            .append(", %struct.ArrayListInt** ").append(fieldPtr).append("\n");
                }
                case "double" -> {
                    String listTmp = temps.newTemp();
                    sb.append("  ").append(listTmp)
                            .append(" = call %struct.ArrayListDouble* @arraylist_create_double(i64 10)\n");

                    sb.append("  store %struct.ArrayListDouble* ").append(listTmp)
                            .append(", %struct.ArrayListDouble** ").append(fieldPtr).append("\n");
                }
                case "boolean", "bool" -> {
                    String listTmp = temps.newTemp();
                    sb.append("  ").append(listTmp)
                            .append(" = call %struct.ArrayListBool* @arraylist_create_bool(i64 10)\n");

                    sb.append("  store %struct.ArrayListBool* ").append(listTmp)
                            .append(", %struct.ArrayListBool** ").append(fieldPtr).append("\n");
                }
                default -> {
                    // genérico: arraylist_create retorna i8* -> bitcast para %ArrayList*
                    String raw = temps.newTemp();
                    String cast = temps.newTemp();

                    sb.append("  ").append(raw)
                            .append(" = call i8* @arraylist_create(i64 10)\n");

                    sb.append("  ").append(cast)
                            .append(" = bitcast i8* ").append(raw)
                            .append(" to %ArrayList*\n");

                    sb.append("  store %ArrayList* ").append(cast)
                            .append(", %ArrayList** ").append(fieldPtr).append("\n");
                }
            }
        }

        // guarda na variável local
        sb.append("  store ").append(structLLVM).append("* ").append(objPtr)
                .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

        return sb.toString();
    }

    private String mapFieldTypeForStruct(String type) {
        if (type.startsWith("List<")) {
            String inner = type.substring(5, type.length() - 1).trim();
            return switch (inner) {
                case "int" -> "%struct.ArrayListInt*";
                case "double" -> "%struct.ArrayListDouble*";
                case "boolean", "bool" -> "%struct.ArrayListBool*";
                default -> "%ArrayList*";
            };
        }
        if (type.startsWith("Struct<")) {
            String inner = type.substring(7, type.length() - 1).trim();
            return "%" + inner + "*";
        }
        return mapper.toLLVM(type);
    }

    private String emitDefaultValue(String type, StringBuilder sb) {
        return switch (type) {
            case "int" -> "0";
            case "double", "float" -> "0.0";
            case "boolean", "bool" -> "0";
            case "string" -> {
                String tmp = temps.newTemp();
                // usa string vazia global
                String empty = visitor.getGlobalStrings().getGlobalName("");
                sb.append("  ").append(tmp)
                        .append(" = call %String* @createString(i8* ")
                        .append(empty).append(")\n");
                yield tmp;
            }
            default -> "null";
        };
    }
}
