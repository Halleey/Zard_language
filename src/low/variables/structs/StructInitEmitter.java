package low.variables.structs;


import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.functions.TypeMapper;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.Map;


public class StructInitEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final Map<String, String> localVars;
    private final TypeMapper  typeMapper= new TypeMapper();
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
    public String emit(VariableDeclarationNode node, TypeInfos info) {

        StringBuilder sb = new StringBuilder();

        String srcType  = info.getSourceType();   // Struct<Row>
        String llvmType = info.getLLVMType();     // %Row*
        String varPtr   = getVarPtr(node.getName());

        // ==== resolver nome da struct ====
        String structName = srcType.substring("Struct<".length(), srcType.length() - 1).trim();
        StructNode structDef = visitor.getStructNode(structName);

        if (structDef == null) {
            throw new RuntimeException("Struct não encontrada: " + structName);
        }

        // %Row*
        String structLLVMPtr = llvmType;
        // %Row
        String structLLVM = structLLVMPtr.substring(0, structLLVMPtr.length() - 1);

        String gepTmp  = temps.newTemp();
        String sizeTmp = temps.newTemp();
        String rawPtr  = temps.newTemp();
        String objPtr  = temps.newTemp();

        // sizeof(%Row)
        sb.append("  ").append(gepTmp)
                .append(" = getelementptr ").append(structLLVM)
                .append(", ").append(structLLVM).append("* null, i32 1\n");

        sb.append("  ").append(sizeTmp)
                .append(" = ptrtoint ").append(structLLVM)
                .append("* ").append(gepTmp).append(" to i64\n");

        // malloc
        sb.append("  ").append(rawPtr)
                .append(" = call i8* @malloc(i64 ").append(sizeTmp).append(")\n");

        // cast
        sb.append("  ").append(objPtr)
                .append(" = bitcast i8* ").append(rawPtr)
                .append(" to ").append(structLLVM).append("*\n");

        sb.append(";;VAL:").append(objPtr)
                .append(";;TYPE:").append(structLLVM).append("*\n");

        var fields = structDef.getFields();

        // ==== inicializa TODOS os campos ====
        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            String fieldType = field.getType().trim();

            // ponteiro para campo i
            String fieldPtr = temps.newTemp();
            sb.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVM).append(", ").append(structLLVM)
                    .append("* ").append(objPtr)
                    .append(", i32 0, i32 ").append(i).append("\n");

            // =========================
            // LIST<T>  -> cria lista nova
            // =========================
            if (fieldType.startsWith("List<") && fieldType.endsWith(">")) {

                String elemType = fieldType.substring(5, fieldType.length() - 1).trim();
                visitor.registerListElementType(node.getName(), elemType);

                String listLLVMType;
                String listCreateFn;

                switch (elemType) {
                    case "int" -> {
                        listLLVMType = "%struct.ArrayListInt*";
                        listCreateFn = "@arraylist_create_int";
                    }
                    case "double" -> {
                        listLLVMType = "%struct.ArrayListDouble*";
                        listCreateFn = "@arraylist_create_double";
                    }
                    case "boolean" -> {
                        listLLVMType = "%struct.ArrayListBool*";
                        listCreateFn = "@arraylist_create_bool";
                    }
                    default -> {
                        // ponteiro genérico (%ArrayList*), usado pra string e structs em listas
                        listLLVMType = "%ArrayList*";
                        listCreateFn = "@arraylist_create";
                    }
                }

                String listTmp = temps.newTemp();

                sb.append("  ").append(listTmp)
                        .append(" = call ").append(listLLVMType)
                        .append(" ").append(listCreateFn)
                        .append("(i64 10)\n");

                sb.append(";;VAL:").append(listTmp)
                        .append(";;TYPE:").append(listLLVMType).append("\n");

                // store no campo (note que fieldPtr é ponteiro pro campo em %Row)
                sb.append("  store ").append(listLLVMType).append(" ").append(listTmp)
                        .append(", ").append(listLLVMType).append("* ").append(fieldPtr).append("\n");

                continue;
            }

            // =========================
            // Struct<X> -> inicia como null
            // =========================
            if (fieldType.startsWith("Struct<") && fieldType.endsWith(">")) {

                String inner = fieldType.substring("Struct<".length(), fieldType.length() - 1).trim();

                StructNode innerDef = visitor.getStructNode(inner);
                if (innerDef == null) {
                    throw new RuntimeException("Struct interna não encontrada: " + inner);
                }

                String innerLLVMName = (innerDef.getLLVMName() != null && !innerDef.getLLVMName().isBlank())
                        ? innerDef.getLLVMName().trim()
                        : inner;

                // campo é um ponteiro (%Inner*)
                sb.append("  store %").append(innerLLVMName).append("* null, %")
                        .append(innerLLVMName).append("** ").append(fieldPtr).append("\n");

                continue;
            }

            // =========================
            // string -> %String* null
            // =========================
            if (fieldType.equals("string") || fieldType.equals("String")) {
                sb.append("  store %String* null, %String** ").append(fieldPtr).append("\n");
                continue;
            }

            // =========================
            // Primitivos -> zero default
            // =========================
            String llvmFieldType = typeMapper.toLLVM(fieldType);

            // floats/doubles usam 0.0, resto 0
            if (llvmFieldType.equals("double")) {
                sb.append("  store double 0.0, double* ").append(fieldPtr).append("\n");
            } else if (llvmFieldType.equals("float")) {
                sb.append("  store float 0.0, float* ").append(fieldPtr).append("\n");
            } else if (llvmFieldType.equals("i1")) {
                sb.append("  store i1 0, i1* ").append(fieldPtr).append("\n");
            } else if (llvmFieldType.equals("i8*")) {
                sb.append("  store i8* null, i8** ").append(fieldPtr).append("\n");
            } else {
                // i32, i64 etc
                sb.append("  store ").append(llvmFieldType).append(" 0, ")
                        .append(llvmFieldType).append("* ").append(fieldPtr).append("\n");
            }
        }

        // ==== guarda no ponteiro da variável ====
        sb.append("  store ").append(structLLVM).append("* ").append(objPtr)
                .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

        return sb.toString();
    }

}